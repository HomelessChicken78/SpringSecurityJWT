package it.itsacademy.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Ruolo {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idRuolo;
    private String nome;
}
