package it.itsacademy.springsecurityjwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * <p>Classe che si occupa di intercettare ogni singola richiesta HTTP in ingresso
 * per verificare la presenza e la validità di un token JWT.</p>
 *
 * <p>Il filtro analizza l'intestazione "Authorization" alla ricerca di un token
 * con prefisso "Bearer ". Se il token è presente, ne estrae il nome utente e
 * ne verifica la validità tramite JwtService. In caso di esito
 * positivo, carica i dettagli dell'utente dal database e lo autentica
 * ufficialmente all'interno del contesto di sicurezza di Spring.</p>
 * <p>Se il token JWT non è presente (per esempio prima della login) il filtro
 * non esegue nessun controllo e lascia che sia la filter chain a controllare
 * che per quell'endpoint non vi sia bisogno di autenticazioni e autorizzazioni</p>
 */
@Component
@AllArgsConstructor
// Estendere OncePerRequestFilter, così che sia compatibile con la filter chain
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    // Siccome i filtri vengono prima della controller, un GlobalExceptionHandler fatto come advice non funzionerebbe.
    // Il server restituirebbe uno status 500 perchè l'errore non arriverebbe mai alla controller e quindi l'advice.
    // Con questo oggetto, possiamo dire di non lasciare la gestione della eccezione a Tomcat, ma di portarla
    // a livello di Spring, come se fosse stata lanciata da una controller.
    private final HandlerExceptionResolver handlerExceptionResolver;

    // Questo è il cuore del filtro, il metodo che intercetta ogni singola richiesta in ingresso.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Estrae la stringa del token dalla richiesta HTTP tramite il metodo di supporto definito in basso.
        String jwt = estraiTokenDallaRequest(request);

        try {
            // Controlla:
            // 1. Che il token sia effettivamente presente. Se non lo è allora il parsing è fallito.
            // 2. Che l'utente non sia già stato autenticato nel contesto di sicurezza per questa specifica richiesta.
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Delega al servizio JWT il compito di leggere il token, controllare che non sia manomesso (se lo è
                // lancia un'eccezione) e prenderne il nome dell'utente.
                String username = jwtService.extractUsername(jwt);

                // Cerca nel database i dettagli completi dell'utente associato a quel nome.
                // In questo modo ci assicuriamo che l'username già esista nel sistema.
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Controlla che il token non sia scaduto e corrisponda effettivamente all'utente.
                if (jwtService.validateToken(jwt, userDetails)) {

                    // Crea l'oggetto di tipo UsernamePasswordAuthenticationToken, che è una implementazione
                    // di Authentication richiesta da Spring Security per registrare un utente come autenticato.
                    // Il costruttore richiede l'inserimento di tre parametri fondamentali:
                    // 1. Il "principal": i dati completi dell'utente. Usiamo quelli presi dal database.
                    // 2. Le "credentials": la password. Qui passiamo "null" poichè il JWT è già stato
                    //    validato e dunque è inutile e rischioso passare la password nel token.
                    // 3. Le "authorities": la lista dei ruoli o permessi associati all'utente (es. "ROLE_ADMIN").
                    //    Questo parametro è obbligatorio per permettere a Spring Security di decidere
                    //    quali endpoint l'utente è autorizzato a visitare durante questa specifica richiesta.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // Aggiunge all'oggetto di autenticazione ulteriori dettagli tecnici legati alla richiesta web,
                    // come ad esempio l'indirizzo IP del client o l'ID della sessione.
                    // In questo modo sappiamo esattamente la provenienza esatta della richiesta:
                    // potrebbe essere usato in futuro per feature di sicurezza avanzate.
                    // WebAuthenticationDetailsSource().buildDetails(request) dice "creami questi dettagli
                    // automaticamente a partire dalla richiesta". Con setDetails metto i dettagli creati nel token.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Salva l'autenticazione nel contesto. In questo modo gli altri metodo, incluse altre chiamate
                    // a questo metodo sanno già che l'utente è già autenticato.
                    // Da questo momento, Spring Security considera l'utente ufficialmente loggato.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Se durante l'analisi del token o l'estrazione dell'utente si verifica un errore,
            // si cattura l'eccezione e la si delega al gestore globale per restituire una risposta HTTP coerente.
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

        // Proseguiamo la catena di filtri se tutto va a buon fine.
        // Senza questa riga, la richiesta si fermerebbe qui e il client rimarrebbe in attesa infinita.
        filterChain.doFilter(request, response);
    }

    // Metodo che estrae il token dalla richiesta http, ignorando il resto
    private String estraiTokenDallaRequest(HttpServletRequest request) {
        // L'oggetto "request" contiene tutto ciò che il client ha inviato al server (body, verbi, endpoint etc.),
        // I token si trovano (per standard) nell'"Header" chiamato "Authorization".
        String headerAuth = request.getHeader("Authorization"); // Ottieni l'header con chiave "Authorization"

        // StringUtils.hasText controlla che l'header esista e non sia vuoto.
        // Il metodo "startsWith" delle stringhe controlla invece che inizia con "Bearer ".
        // Esistono vari metodi per autenticarsi sul web (es. "Basic" per le password classiche).
        // La parola "Bearer " indica che a seguire sarà indicato chi è il portatore del token.
        // La stringa headerAuth è dunque "Bearer 324uiyebttbedashj7287b"
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Ci interessa solo sapere 324uiyebttbedashj7287b di headerAuth, non ci interessa di "Bearer ".
            // Il comando substring(7) ordina a Java di tagliare via i primi 7 caratteri,
            // ovvero quelli corrispondenti alla parola "Bearer ".
            return headerAuth.substring(7);
        }

        // Nel caso le condizioni non si verificano, ritorna null.
        return null;
    }
}