package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.*;
import it.itsacademy.springsecurityjwt.entity.Ruolo;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.exception.NotFoundException;
import it.itsacademy.springsecurityjwt.mapper.UtenteMapper;
import it.itsacademy.springsecurityjwt.repository.*;
import it.itsacademy.springsecurityjwt.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // Dependency Injection in automatico
@Service @Transactional
public class AuthServiceImpl implements AuthService {
    private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;
    private final UtenteMapper mapper;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private AuthenticationManager authenticationManager;

    @Override
    public TokenDTO signUp(SignUpUtenteDTO signUpRequest) {
        if (utenteRepository.existsByUsername(signUpRequest.getUsername()))
            throw new RuntimeException("Username già in uso!");

        Utente utenteDaCreare = mapper.toEntity(signUpRequest);
        utenteDaCreare.setActive(true);

        // Cifriamo la password: in questo modo nel db salviamo una stringa illeggibile anziché la vera password.
        // Solo l'encripter stesso potrà confrontare la password digitata con la password salvata nel db.
        String passwordCifrata = encoder.encode(utenteDaCreare.getPassword());
        utenteDaCreare.setPassword(passwordCifrata);

        // Rendi l'utente "USER"
        Ruolo ruoloUser = ruoloRepository.findByTipo(Ruolo.TipoRuolo.USER)
                .orElseThrow(
                        () -> new NotFoundException("Errore nella creazione dell'utente: non esiste il ruolo \"USER\"")
                );
        utenteDaCreare.getSetRuoli().add(ruoloUser);

        Utente utenteCreato = utenteRepository.save(utenteDaCreare); // Salva l'utente nel db

        // Crea il jwt
        String jwtCreato = jwtService.createToken(utenteCreato.getUsername());

        // Ritorna il jwt in un dto apposito
        return new TokenDTO(jwtCreato);
    }

    @Override
    public TokenDTO login(LoginUtenteDTO loginRequest) {
        // Cerca l'utente direttamente nel db tramite il repository
        Utente utenteTrovato = utenteRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenziali non valide: username inesistente."));

        // L'encoder confronta se la password criptata nel db e la password nel dto passato (criptandola) coincidono
        boolean passwordCoincide = encoder.matches(loginRequest.getPassword(), utenteTrovato.getPassword());

        if (!passwordCoincide)
            throw new RuntimeException("Credenziali non valide: password errata.");

        // Crea il jwt
        String jwtCreato = jwtService.createToken(utenteTrovato.getUsername());

        // Ritorna il jwt in un dto apposito
        return new TokenDTO(jwtCreato);
    }
}
