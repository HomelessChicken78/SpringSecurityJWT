package it.itsacademy.springsecurityjwt.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Indica a Spring: “La classe contiene la configurazione dei bean, cioè come creare oggetti gestiti da Spring.”
@AllArgsConstructor
@EnableWebSecurity // Si occupa di preimpostare alcune impostazioni e alcuni bean. Tra questi, genera l'oggetto HttpSecurity,
                    // un builder che ci verrà iniettato per definire le nostre regole e creare un oggetto di tipo SecurityFilterChain.
public class SecurityConfiguration {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Inizializziamo un bean per l'encoder che si occuperà della password.
     * Quando si chiede di fare la Dependency Injection di un tipo PasswordEncoder,
     * spring ritornerà questo bean.
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(); // Diciamo a spring che nello specifico vogliamo questo encoder
    }

    /**
     * <p>Un Identity Provider (IdP) è un sistema che si occupa di autenticare l'identità di un utente già registrato.
     * Esistono IdP esterni/federati (come Google, Apple o SPID), in cui l'applicazione delega il controllo
     * a terzi, e IdP interni/locali (come il nostro), in cui l'applicazione gestisce autonomamente credenziali e database.</p>
     * <p>Questo Bean definisce il nostro Identity Provider locale basato su Database.</p>
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        /*
         * Creiamo il DaoAuthenticationProvider. La variabile "userDetailsService" è final e siamo forzati
         * a passargli il "customUserDetailsService" nel costruttore.
         * Il provider deve sapere dove si trovano i dati. Ha bisogno dell'UserDetailsService per cercare sul DB,
         * l'utente tramite username e ottenere l'UserDetails contenente la crittografia della password
         * e le authorities.
         */
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);

        /*
         * Colleghiamo il PasswordEncoder. Una volta che l'UserDetailsService ha prelevato l'utente dal database,
         * il provider usa questo encoder per prendere la password digitata in chiaro dall'utente, applicarvi
         * l'algoritmo hashing e vedere se è uguale con l'hash memorizzato sul DB.
         */
        authProvider.setPasswordEncoder(getPasswordEncoder());

        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http // DI dell'HttpSecurityConfiguration grazie a EnableWebSecurity
    ) {
        http.csrf(AbstractHttpConfigurer::disable) // Disabilitiamolo poichè inutile per le API stateless
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests( // Definiamo le regole di accesso per gli URL
                // NB: le regole più specifiche vanno più in alto o bloccherebbe le altre
                  a ->
                          // Permetti tutti gli endpoint di autenticazione senza avere autenticazioni
                          a.requestMatchers("/auth/**").permitAll()

                          // Per fare GET /api/utenti l'utente deve essere admin
                          .requestMatchers(HttpMethod.GET, "/utenti").hasAuthority("ADMIN")

                          // Per gli altri devi essere almeno autenticato
                          .anyRequest().authenticated()
                )

                // Diciamo al server di non salvare cookie
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Da questo momento, quando qualcuno tenta di fare il login, voglio che tu usi questo strumento
                // per verificare se ha il diritto di entrare
                .authenticationProvider(authenticationProvider())

                // Aggiungiamo il nostro filtro: controlla che abbia una jwt. Se ce l'ha, aggiunge al contesto
                // (a quella chiamata http) un oggetto Authentication. Se non ce l'ha lascia comunque che la richiesta passi,
                // così che ".authorizeHttpRequests" controlli se è necessario che l'utente sia autenticato
                // (nel contesto ci sia Authentication) per quell'endpoint
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Impostiamo le regole dell'HttpSecurity tramite il metodo ".build". Questo si occupa di eseguire vari
        // controlli sulle regole da noi definite e aggiungere ulteriori regole di sicurezza
        // prima di ritornare un oggetto di tipo SecurityFilterChain.
        return http.build();
    }
}
