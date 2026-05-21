package it.itsacademy.springsecurityjwt.mapper;

import it.itsacademy.springsecurityjwt.dto.*;
import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.exception.BadRequestException;
import org.mapstruct.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UtenteMapper {
    @Mapping(target = "idUtente", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "setRuoli", ignore = true)
    Utente toEntity(SignUpUtenteDTO signUpDto);

    @Mapping(target = "idUtente", ignore = true)
    @Mapping(target = "nome", ignore = true)
    @Mapping(target = "cognome", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "setRuoli", ignore = true)
    Utente toEntity(LoginUtenteDTO loginDto);

    @Mapping(target = "idUtente", ignore = true)
    @Mapping(target = "setRuoli", source = "setRuoli",
            qualifiedByName = "setStringToSetRuoli" // Serve a specificare una funzione che verrà usata per mappare SetRuoli
    )
    Utente toEntity(UtenteDTO dto);

    @Mapping(target = "setRuoli", source = "setRuoli",
            qualifiedByName = "setRuoloToSetString" // Serve a specificare una funzione che verrà usata per mappare SetRuoli
    )
    UtenteDTO toDTO(Utente entity);

    Collection<UtenteDTO> toDTO(Collection<Utente> entities);

    // Gestisci la trasformazione da un set di ruolo a un set di stringhe
    @Named(value = "setRuoloToSetString")
    default Set<String> setRuoloToSetString(Set<Ruolo> entityRuolo) {
        if (entityRuolo == null) return null;

        return entityRuolo.stream()
                .map(r -> r.getTipo().name())
                .collect(Collectors.toSet());
    }

    // Gestisce la trasformazione da un set di stringhe a un set di ruoli
    @Named(value = "setStringToSetRuoli")
    default Set<Ruolo> setStringToSetRuoli(Set<String> tipiRuolo) {
        if (tipiRuolo == null) return null;

        return tipiRuolo.stream()
                .map(t -> {
                    try {
                        return new Ruolo(null, Ruolo.TipoRuolo.valueOf(t));
                    } catch (IllegalArgumentException e) {
                        // Se un ruolo non esiste, rilancia un'eccezione personalizzata e aggiungigli un messaggio
                        throw new BadRequestException("Valore per " + t + " non è un ruolo", e);
                    }
                })
                .collect(Collectors.toSet());
    }
}
