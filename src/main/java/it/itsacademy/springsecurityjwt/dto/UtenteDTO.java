package it.itsacademy.springsecurityjwt.dto;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UtenteDTO {
    private String username, nome, cognome;
    private Set<String> setRuoli;
}