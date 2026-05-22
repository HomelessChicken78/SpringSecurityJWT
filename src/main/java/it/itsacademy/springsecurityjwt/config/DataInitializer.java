package it.itsacademy.springsecurityjwt.config;

import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.UtenteDTO;
import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.repository.RuoloRepository;
import it.itsacademy.springsecurityjwt.repository.UtenteRepository;
import it.itsacademy.springsecurityjwt.service.AuthService;
import it.itsacademy.springsecurityjwt.service.UtenteService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Componente responsabile delle operazioni di inizializzazione del database all'avvio dell'applicazione.
 * Ispeziona automaticamente e popola i ruoli di sistema mancanti dalla definizione di RuoloEnum.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UtenteRepository utenteRepository;
    private final AuthService authService; // Cosi non viene duplicata la logica di creazione
    private final RuoloRepository ruoloRepository;

    @Value("${initial-data.admin.username}")
    private String adminUsername;

    @Value("${initial-data.admin.password}")
    private String adminPassword;

    /**
     * Viene eseguito immediatamente dopo il caricamento del contesto dell'applicazione.
     * Itera su tutte le enumerazioni dei ruoli definiti e li salva se non sono già presenti.
     * Cerca se ci sta almeno un utente admin e se non ci sta lo crea.
     *
     * @param args Argomenti della riga di comando passati all'applicazione
     */
    @Override
    public void run(String @NonNull ... args) {
        // Crea i ruoli inesistenti
        Arrays.stream(Ruolo.TipoRuolo.values())
                .forEach(nomeRuolo -> {
                    if (ruoloRepository.findByTipo(nomeRuolo).isEmpty()) {
                        Ruolo nuovoRuolo = Ruolo.builder()
                                .tipo(nomeRuolo)
                                .build();
                        ruoloRepository.save(nuovoRuolo);
                    }
                });

        // Crea l'admin
        // Controlla che non esista già un admin
        if (utenteRepository.findByTipoRuolo(Ruolo.TipoRuolo.ADMIN).isEmpty()) {
            // Crea il dto per chiedere la creazione dell'admin
            SignUpUtenteDTO nuovoAdmin = new SignUpUtenteDTO(adminUsername, adminPassword, "Giacomo", "Coccodrillini");

            // Crea l'admin usando la service. In questo momento viene creato con ruolo "USER"
            authService.signUp(nuovoAdmin);
            Utente adminCreato = utenteRepository.findByUsernameOrThrow(adminUsername); // Trova l'utente appena creato

            // Crea i ruoli del nuovo utente
            adminCreato.getSetRuoli().add(ruoloRepository.findByTipo(Ruolo.TipoRuolo.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Non esiste il ruolo admin. Non posso creare un utente admin")));
            adminCreato.getSetRuoli().add(ruoloRepository.findByTipo(Ruolo.TipoRuolo.VIEWER)
                    .orElseThrow(() -> new RuntimeException("Non esiste il ruolo viewer. Non posso creare un utente admin")));

            utenteRepository.save(adminCreato);
        }
    }
}