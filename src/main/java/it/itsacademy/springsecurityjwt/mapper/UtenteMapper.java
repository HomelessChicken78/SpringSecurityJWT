package it.itsacademy.springsecurityjwt.mapper;

import it.itsacademy.springsecurityjwt.dto.LoginUtenteDTO;
import it.itsacademy.springsecurityjwt.dto.SignUpUtenteDTO;
import it.itsacademy.springsecurityjwt.entity.Utente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtenteMapper {
    Utente toEntity(SignUpUtenteDTO signUpDto);

    Utente toEntity(LoginUtenteDTO loginDto);
}
