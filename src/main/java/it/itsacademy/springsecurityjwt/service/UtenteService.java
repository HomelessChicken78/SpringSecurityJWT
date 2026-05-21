package it.itsacademy.springsecurityjwt.service;

import it.itsacademy.springsecurityjwt.dto.UtenteDTO;

import java.util.Collection;
import java.util.UUID;

public interface UtenteService {
    Collection<UtenteDTO> getAllUtenti();

    UtenteDTO getUtenteByUsername(String username);

    UtenteDTO updateUtente(String username, UtenteDTO utenteDTO);

    void deleteUtente(String username);
}
