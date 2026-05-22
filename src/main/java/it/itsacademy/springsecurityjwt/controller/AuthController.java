package it.itsacademy.springsecurityjwt.controller;

import it.itsacademy.springsecurityjwt.dto.LoginUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.TokenDTO;
import it.itsacademy.springsecurityjwt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final String json = "application/json";
    private final AuthService service;

    @PostMapping(path = "/signUp", consumes = json, produces = json)
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO signUp(@RequestBody @Valid SignUpUtenteDTO signUpDto) {
        return service.signUp(signUpDto);
    }

    @PostMapping(path = "/login", consumes = json, produces = json)
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO login(@RequestBody @Valid LoginUtenteDTO loginDto) {
        return service.login(loginDto);
    }
}
