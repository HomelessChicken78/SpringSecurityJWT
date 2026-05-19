package it.itsacademy.springsecurityjwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Ruolo {
    @Id @GeneratedValue private UUID idRuolo;
    private String nome;
}
