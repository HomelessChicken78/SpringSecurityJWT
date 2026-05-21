package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.LoginUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.TokenDTO;

public interface AuthService {
    TokenDTO login(LoginUtenteDTO loginRequest);
    TokenDTO signUp(SignUpUtenteDTO signUpRequest);
}
