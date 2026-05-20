package it.itsacademy.springsecurityjwt.repository;

import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository extends JpaRepository<Utente, UUID> {
    Optional<Utente> findByUsername(String username);
    default Utente findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Utente con username \"" + username + "\" non trovato."));
    }
    boolean existsByUsername(String username);
    Optional<Utente> findByIdUtente(UUID idUtente);
}
