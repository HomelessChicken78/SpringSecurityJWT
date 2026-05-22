package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.PasswordChangeDTO;
import it.itsacademy.springsecurityjwt.dto.UtenteDTO;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.exception.BadRequestException;
import it.itsacademy.springsecurityjwt.mapper.UtenteMapper;
import it.itsacademy.springsecurityjwt.repository.UtenteRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UtenteServiceImpl implements UtenteService {
    private final UtenteRepository utenteRepository;
    private final UtenteMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public Collection<UtenteDTO> getAllUtenti() {
        return mapper.toDTO(utenteRepository.findAll());
    }

    @Override
    public UtenteDTO getUtenteByUsername(String username) {
        // Cerca l'utente usando il suo username o solleva un exception se non esiste
        Utente utente = utenteRepository.findByUsernameOrThrow(username);

        return mapper.toDTO(utente);
    }

    @Override
    public UtenteDTO getMyself() {
        // Cerca nell'autenticazione l'utente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Controlla che l'utente sia autenticato
        if (!auth.isAuthenticated() || auth == null)
            throw new RuntimeException("Errore: Utente non autenticato!");

        // Cerca nel database l'utente
        Utente trovato = utenteRepository.findByUsernameOrThrow(auth.getName());
        return mapper.toDTO(trovato);
    }

    @Override
    public UtenteDTO updateUtente(String username, UtenteDTO utenteDTO) {//UPDATE
        // Verifica l'esistenza dell'utente da modificare
        Utente utente = utenteRepository.findByUsernameOrThrow(username);

        // Aggiorna i campi autorizzati
        utente.setUsername(utenteDTO.getNome());
        utente.setCognome(utenteDTO.getCognome());

        // Salva le modifiche in db via Hibernate
        Utente updatedUtente = utenteRepository.save(utente);

        // Torna l'oggetto aggiornato e lo converte in DTO
        return mapper.toDTO(updatedUtente);
    }

    @Override
    public UtenteDTO changePassword(PasswordChangeDTO newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Controlla che l'utente sia autenticato
        if (!auth.isAuthenticated() || auth == null)
            throw new RuntimeException("Errore: Utente non autenticato! Impossibile cambiare la password.");

        String usernameAutenticato = auth.getName(); // Chiama automaticamente auth.getPrincipal().getUsername()
        Utente trovato = utenteRepository.findByUsernameOrThrow(usernameAutenticato); // Cerca l'utente

        // Cifriamo la password: in questo modo nel db salviamo una stringa illeggibile anziché la vera password.
        // Solo l'encripter stesso potrà confrontare la password digitata con la password salvata nel db.
        String passwordCifrata = encoder.encode(newPassword.getPassword());
        trovato.setPassword(passwordCifrata);

        // Salva l'utente
        Utente salvato = utenteRepository.save(trovato);

        return mapper.toDTO(salvato);
    }

    @Override
    public void deleteUtente(String username) {//DELETE
        // Cerca l'utente
        Utente trovato = utenteRepository.findByUsernameOrThrow(username);

        // Se è già cancellato lancia un'eccezione
        if (!trovato.getActive())
            throw new BadRequestException("L'utente è già cancellato");

        // Cancella l'utente corrispondente all'username fornito
        // NB: Non viene cancellato per davvero ma marcato come non attivo
        trovato.setActive(false);
    }
}