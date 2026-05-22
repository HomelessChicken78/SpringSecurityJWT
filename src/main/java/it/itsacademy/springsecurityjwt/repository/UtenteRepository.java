package it.itsacademy.springsecurityjwt.repository;

import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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

    /**
     Cerca tutti gli utenti che hanno un determinato ruolo
     @param tipoRuoloCercato il nome della tipologia di ruolo da cercare
     */
    @Query("SELECT DISTINCT ut FROM Utente AS ut JOIN ut.setRuoli r WHERE r.tipo = :tipoRuoloCercato AND ut.active = true")
    List<Utente> findByTipoRuolo(Ruolo.TipoRuolo tipoRuoloCercato);

    /**
     * Cerca tutti gli utenti non cancellati del sistema
     */
    @Query("SELECT ut FROM Utente AS ut WHERE ut.active = true")
    List<Utente> findAllNotDeleted();
}
