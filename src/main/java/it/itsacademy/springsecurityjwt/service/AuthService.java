package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.LoginUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;

public interface AuthService {
    void login(LoginUtenteDTO loginRequest);
    void signUp(SignUpUtenteDTO signUpRequest);
}
