package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.LoginUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;
import it.itsacademy.springsecurityjwt.entity.Utente;
import it.itsacademy.springsecurityjwt.mapper.UtenteMapper;
import it.itsacademy.springsecurityjwt.repository.RuoloRepository;
import it.itsacademy.springsecurityjwt.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // Dependency Injection in automatico
@Service @Transactional
public class AuthServiceImpl implements AuthService {
    private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;
    private final UtenteMapper mapper;
    private AuthenticationManager authenticationManager;
   // private final PasswordEncoder encoder; // TODO togli il commento quando il password encoder è inizializzato

    @Override
    public void signUp(SignUpUtenteDTO signUpRequest) {
        if (utenteRepository.existsByUsername(signUpRequest.getUsername()))
            throw new RuntimeException("Username già in uso!");

        Utente utenteCreato = mapper.toEntity(signUpRequest);
        utenteCreato.setActive(true);

        // Cifriamo la password: in questo modo nel db salviamo una stringa illeggibile anziché la vera password.
        // Solo l'encripter stesso potrà confrontare la password digitata con la password salvata nel db.
        // TODO togli il commento quando il password encoder è inizializzato
       // String passwordCifrata = encoder.encode(utenteCreato.getPassword());
       // utenteCreato.setPassword(passwordCifrata);
    }

    @Override
    public void login(LoginUtenteDTO loginRequest) {
        // Cerca l'utente direttamente nel db tramite il repository
        Utente utente = utenteRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenziali non valide: username inesistente."));

        // L'encoder confronta se la password criptata nel db e la password nel dto passato (criptandola) coincidono
        /*boolean passwordCoincide = encoder.matches(loginRequest.getPassword(), utente.getPassword()); // TODO togli il commento quando il password encoder è inizializzato

        if (!passwordCoincide) {
            throw new RuntimeException("Credenziali non valide: password errata.");
        }*/
    }
}
