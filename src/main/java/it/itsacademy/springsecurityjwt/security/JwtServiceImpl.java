package it.itsacademy.springsecurityjwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe di servizio che si occupa di effettuare i controlli
 * crittografici sui JWT. Analizza la stringa di testo del token
 * per verificarne l'autenticità.
 */
@Service
public class JwtServiceImpl implements JwtService {
    // Questa variabile ospita la chiave segreta (una stringa di testo apparentemente casuale)
    // definita nel file "application.properties". È la password segreta del server.
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationMillis;

    /**
     * Metodo che riceve il token e restituisce il nome utente scritto al suo interno.
     */
    // Il jwt è una stringa separata da tre punti "." per poter dividere il token in tre punti.
    // Il primo contiene l'header che contiene diversi meta tag (che si tratta di un jwt, quale algoritmo usa ecc.)
    // Il secondo contiene i veri dati come il subject (l'utente), e le authorities.
    // Il terzo blocco contiene la firma. Il server possiede una chiave jwt segreta, grazie alla quale codifica
    // il messaggio della seconda parte e ottiene una firma. Se la firma attesa e quella scritta non corrispondono
    // il messaggio è stato manomesso.
    // Poichè il token contiene i dati veri e non ha meccanismi di sicurezza per la lettura
    // (ne ha solo per prevenire che venga modificato da malintenzionati), può essere letto da chiunque.
    // Per questo motivo non si mettono dati come la password all'interno del token.
    @Override
    public String extractUsername(String jwt) {
        // La chiave segreta nel file di configurazione è salvata in formato Base64
        // (un sistema che trasforma i dati binari in testo leggibile).
        // L'algoritmo crittografico non può usare il testo puro, ha bisogno dei byte originali.
        // Questo prende la stringa di testo e la riconverte in un array di byte grezzi.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // Prende l'array di byte appena generato e lo trasforma in un oggetto "Key" ufficiale di Java,
        // configurandolo specificamente per l'algoritmo HMAC (il sistema matematico che gestisce le firme).
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // Inizia il builder per configurare il lettore di token.
        return Jwts.parserBuilder()
                // Mette la chiave segreta appena creata. Quest'ultima verrà usata
                // per ricalcolare la firma del token e verificare che nessuno lo abbia manomesso.
                .setSigningKey(key)
                .build()

                // Analizza la stringa del token. In questa riga avviene il vero controllo di sicurezza:
                // la libreria prende la firma in fondo al token e la confronta con la chiave segreta.
                // Se il token è falso o alterato, il programma si blocca immediatamente lanciando un'eccezione.
                .parseClaimsJws(jwt)

                // Se la firma è valida, questo comando estrae il "Body",
                // ovvero la sezione centrale del token che contiene la mappa con tutti i dati dell'utente.
                .getBody()

                // All'interno dei dati, cerca il campo standard chiamato "Subject".
                // Per convenzione internazionale, in questo campo si inserisce l'identificativo unico
                // dell'utente, che nel nostro caso corrisponde all'username o all'email.
                .getSubject();
    }

    /**
     * Metodo che verifica se il token appartiene all'utente corretto e se è ancora utilizzabile.
     */
    @Override
    public boolean validateToken(String jwt, UserDetails userDetails) {
        // Recupera lo username scritto dentro il token.
        String username = extractUsername(jwt);

        // Il metodo esegue una verifica booleana (restituisce true o false) basata su due controlli:
        // 1. "username.equals(userDetails.getUsername())" (dove username è quello estratto dal jwt)
        //    che controlla se il nome utente estratto dal token corrisponde al nome dell'utente che si
        //    trova nell'oggetto passato come parametro (che dovrebbe essere preso dal database).
        // 2. "!isTokenExpired(jwt)" Controlla che il token non sia scaduto.
        // Se entrambe le condizioni sono vere, il metodo ritorna true.
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
    }

    @Override
    public String createToken(String username, Map<String, Object> extraClaims) {
        // Stessa cosa di prima per creare la chiave segreta
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMillis))
                .signWith(key)
                .compact();
    }

    @Override
    public String createToken(String username) {
        return createToken(username, new HashMap<>());
    }

    /**
     * Metodo privato che calcola se il tempo massimo di validità del token è stato superato.
     */
    private boolean isTokenExpired(String token) {
        // Stessa logica di prima per decodificare la chiave
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date expirationDate = Jwts.parserBuilder()
                // Fornisce la chiave segreta e valida la firma (stessa logica del metodo extractUsername
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)

                // Se la firma è valida, questo comando estrae il "Body",
                // ovvero la sezione centrale del token che contiene la mappa con tutti i dati dell'utente.
                .getBody()

                // Chiama il metodo specifico "getExpiration()".
                // Questo metodo legge il campo standard chiamato "Expiration" (abbreviato in "exp"),
                // che contiene la data e l'ora esatta in cui il token smetterà di funzionare.
                .getExpiration();

        // "new Date()" genera un oggetto che contiene la data e l'ora esatta di questo preciso istante.
        // Il metodo "before" controlla se la data di scadenza del token viene prima del momento attuale.
        // Se la data di scadenza viene prima di "adesso", significa che il tempo è scaduto,
        // quindi il metodo restituisce true: il token è scaduto.
        return expirationDate.before(new Date());
    }
}