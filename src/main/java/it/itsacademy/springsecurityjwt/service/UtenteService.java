package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.PasswordChangeDTO;
import it.itsacademy.springsecurityjwt.dto.UtenteDTO;

import java.util.Collection;

public interface UtenteService {
    Collection<UtenteDTO> getAllUtenti();

    UtenteDTO getUtenteByUsername(String username);

    UtenteDTO getMyself();

    UtenteDTO updateUtente(String username, UtenteDTO utenteDTO);

    UtenteDTO changePassword(PasswordChangeDTO newPassword);

    void deleteUtente(String username);
}
