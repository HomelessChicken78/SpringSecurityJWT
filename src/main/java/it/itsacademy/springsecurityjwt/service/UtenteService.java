package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.PasswordChangeDTO;
import it.itsacademy.springsecurityjwt.dto.RuoloDTO;
import it.itsacademy.springsecurityjwt.dto.UtenteDTO;
import jakarta.validation.Valid;

import java.util.Collection;

public interface UtenteService {
    Collection<UtenteDTO> getAllUtenti();

    UtenteDTO getUtenteByUsername(String username);

    UtenteDTO getMyself();

    UtenteDTO updateUtente(String username, UtenteDTO utenteDTO);

    UtenteDTO changePassword(PasswordChangeDTO newPassword);

    UtenteDTO grantRole(String username, RuoloDTO newRole);

    UtenteDTO revokeRole(String username, @Valid RuoloDTO newRole);

    void deleteUtente(String username);
}
