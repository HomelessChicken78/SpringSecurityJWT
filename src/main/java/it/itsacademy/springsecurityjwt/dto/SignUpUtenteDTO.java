package it.itsacademy.springsecurityjwt.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpUtenteDTO {
    @NotNull(message = "{utente.username.obbligatorio}")
    @NotEmpty(message = "{utente.username.obbligatorio}")
    private String username;

    @NotNull(message = "{utente.password.obbligatorio}")
    @NotEmpty(message = "{utente.password.obbligatorio}")
    private String password;

    @NotNull(message = "{utente.nome.obbligatorio}")
    @NotEmpty(message = "{utente.nome.obbligatorio}")
    private String nome;

    @NotNull(message = "{utente.cognome.obbligatorio}")
    @NotEmpty(message = "{utente.cognome.obbligatorio}")
    private String cognome;
}
