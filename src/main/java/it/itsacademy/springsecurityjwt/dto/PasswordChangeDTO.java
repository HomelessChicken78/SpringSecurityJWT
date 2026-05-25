package it.itsacademy.springsecurityjwt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PasswordChangeDTO {
    @NotNull(message = "{utente.password.obbligatorio}")
    @NotEmpty(message = "{utente.password.obbligatorio}")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-.]).{8,}$",
        message = "{utente.password.validata}")
    private String password;
}
