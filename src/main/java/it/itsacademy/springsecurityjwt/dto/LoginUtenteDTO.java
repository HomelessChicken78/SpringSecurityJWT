package it.itsacademy.springsecurityjwt.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class LoginUtenteDTO {
    @NotNull(message = "{utente.username.obbligatorio}")
    @NotEmpty(message = "{utente.username.obbligatorio}")
    private String username;

    @NotNull(message = "{utente.password.obbligatorio}")
    @NotEmpty(message = "{utente.password.obbligatorio}")
    private String password;
}
