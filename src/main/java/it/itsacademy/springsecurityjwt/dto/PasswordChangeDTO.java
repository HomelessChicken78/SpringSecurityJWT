package it.itsacademy.springsecurityjwt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PasswordChangeDTO {
    @NotNull(message = "{utente.password.obbligatorio}")
    @NotEmpty(message = "{utente.password.obbligatorio}")
    private String password;
}
