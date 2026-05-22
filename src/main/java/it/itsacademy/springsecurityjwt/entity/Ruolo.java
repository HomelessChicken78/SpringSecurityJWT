package it.itsacademy.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter @EqualsAndHashCode(of = "idRuolo")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Ruolo {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idRuolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoRuolo tipo;

    public enum TipoRuolo {
        ADMIN,
        USER,
        VIEWER
    }
}
