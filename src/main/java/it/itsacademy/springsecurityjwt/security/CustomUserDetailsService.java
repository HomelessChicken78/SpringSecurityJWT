package it.itsacademy.springsecurityjwt.security;

import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Metodo helper per trasformare i ruoli (presenti nel db) in granted authorities (richieste da spring)
     */
    private List<?extends GrantedAuthority> ruoliToGrantedAuthorities(Set<Ruolo> ruoli) {
        // Crea una lista di authorities a partire da un array di stringhe
        return AuthorityUtils.createAuthorityList(
                ruoli.stream()
                .map(ruolo -> ruolo.getTipo().name()).toArray(String[]::new) // Prendi il tipo di ruolo e mettilo in un array di stringhe
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cerca l'utente nel database
        Utente utenteNelDB = utenteRepository.findByUsernameOrThrow(username);

        // Convertilo "a mano" in uno user details di spring security
        return new User(
              utenteNelDB.getUsername(),
              utenteNelDB.getPassword(),
              utenteNelDB.getActive(),
                true,
                true,
                !utenteNelDB.getActive(),
                new HashSet<>() // TODO momentaneamente messo cosi poichè hibernate ha problemi nel caricamento delle entità
        );
    }
}
