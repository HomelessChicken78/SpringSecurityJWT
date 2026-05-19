package it.itsacademy.springsecurityjwt.repository;

import it.itsacademy.springsecurityjwt.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository extends JpaRepository<Utente, UUID> {
    Optional<Utente> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Utente> findByIdUtente(UUID idUtente);
}
