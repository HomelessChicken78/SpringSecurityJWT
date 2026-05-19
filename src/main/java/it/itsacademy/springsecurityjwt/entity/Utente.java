package it.itsacademy.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Utente {
    @Id @GeneratedValue private UUID idUtente;
    private String username, password, nome, cognome;

    @ManyToMany
    @JoinTable(
            name = "utente_ruolo",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRuolo")
    )
    private Set<Ruolo> setRuoli;
}
