package it.itsacademy.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Utente {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idUtente;

    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private String nome;
    @Column(nullable = false) private String cognome;
    @Column(nullable = false) private Boolean isActive;

    @ManyToMany
    @JoinTable(
            name = "utente_ruolo",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRuolo")
    )
    private Set<Ruolo> setRuoli;
}
