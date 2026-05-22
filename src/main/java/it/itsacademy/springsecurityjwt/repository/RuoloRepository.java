package it.itsacademy.springsecurityjwt.repository;

import it.itsacademy.springsecurityjwt.entity.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RuoloRepository extends JpaRepository<Ruolo, UUID> {
    Optional<Ruolo> findByTipo(Ruolo.TipoRuolo tipo);

    default Ruolo findByTipoOrThrow(Ruolo.TipoRuolo tipo) {
        return findByTipo(tipo)
                .orElseThrow(() -> new RuntimeException("Non esiste il ruolo " + tipo));
    }
}
