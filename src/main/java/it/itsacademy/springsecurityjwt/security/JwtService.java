package it.itsacademy.springsecurityjwt.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Classe di servizio che si occupa di effettuare i controlli
 * crittografici sui JWT. Analizza la stringa di testo del token
 * per verificarne l'autenticità.
 */
public interface JwtService {
    /**
     * Metodo che riceve il token e restituisce il nome utente scritto al suo interno.
     */
    String extractUsername(String jwt);

    /**
     * Metodo che verifica se il token appartiene all'utente corretto e se è ancora utilizzabile.
     */
    boolean validateToken(String jwt, UserDetails userDetails);

    String createToken(String username, Map<String, Object> extraClaims);

    String createToken(String username);
}
