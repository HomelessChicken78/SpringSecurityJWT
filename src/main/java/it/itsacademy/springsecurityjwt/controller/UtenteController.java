package it.itsacademy.springsecurityjwt.controller;

import it.itsacademy.springsecurityjwt.dto.PasswordChangeDTO;
import it.itsacademy.springsecurityjwt.dto.RuoloDTO;
import it.itsacademy.springsecurityjwt.dto.UtenteDTO;
import it.itsacademy.springsecurityjwt.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/utenti")
@RequiredArgsConstructor
public class UtenteController {
    private final UtenteService service;
    private final String json = "application/json";

    @GetMapping(produces = json)
    public Collection<UtenteDTO> getAllUtenti() {
        return service.getAllUtenti();
    }

    @GetMapping(path = "/{username}", produces = json)
    public UtenteDTO getUtenteByUsername(@PathVariable String username) {
        return service.getUtenteByUsername(username);
    }

    @GetMapping(path = "/me", produces = json)
    public UtenteDTO getMyself() {
        return service.getMyself();
    }

    @PatchMapping(path = "/{username}", consumes = json, produces = json)
    public UtenteDTO updateUtente(@PathVariable String username, @Valid @RequestBody UtenteDTO updatedUtente) {
        return service.updateUtente(username, updatedUtente);
    }

    @PatchMapping(path = "/me/password", consumes = json, produces = json)
    public UtenteDTO changePassword(@Valid @RequestBody PasswordChangeDTO newPassword) {
        return service.changePassword(newPassword);
    }

    @PatchMapping(path = "/{username}/roles", consumes = json, produces = json)
    public UtenteDTO grantRole(@PathVariable String username, @Valid @RequestBody RuoloDTO newRole) {
        return service.grantRole(username, newRole);
    }

    @DeleteMapping(path = "/{username}/roles", consumes = json, produces = json)
    public UtenteDTO revokeRole(@PathVariable String username, @Valid @RequestBody RuoloDTO newRole) {
        return service.revokeRole(username, newRole);
    }

    @DeleteMapping(path = "/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUtente(@PathVariable String username) {
        service.deleteUtente(username);
    }
}
