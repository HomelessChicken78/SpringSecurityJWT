package it.itsacademy.springsecurityjwt.config;

import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.repository.RuoloRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Componente responsabile delle operazioni di inizializzazione del database all'avvio dell'applicazione.
 * Ispeziona automaticamente e popola i ruoli di sistema mancanti dalla definizione di RuoloEnum.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RuoloRepository ruoloRepository;

    /**
     * Viene eseguito immediatamente dopo il caricamento del contesto dell'applicazione.
     * Itera su tutte le enumerazioni dei ruoli definiti e li salva se non sono già presenti.
     *
     * @param args Argomenti della riga di comando passati all'applicazione
     */
    @Override
    public void run(String @NonNull ... args) {
        Arrays.stream(Ruolo.TipoRuolo.values())
                .forEach(nomeRuolo -> {
                    if (ruoloRepository.findByTipo(nomeRuolo).isEmpty()) {
                        Ruolo nuovoRuolo = Ruolo.builder()
                                .tipo(nomeRuolo)
                                .build();
                        ruoloRepository.save(nuovoRuolo);
                    }
                });
    }
}