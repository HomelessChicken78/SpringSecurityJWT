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
    @Column(nullable = false) private Boolean active;

    @ManyToMany(
            // Questo serve a dire a hibernate: quando carichi un Utente, carica anche tutti i suoi ruoli.
            // Hibernate, quando si cercano delle entità del database apre una sessione.
            // La sessione viene chiusa dopo un pò.
            // Normalmente in OneToMany e ManyToMany di default Hibernate ha un comportamento LAZY.
            // Ciò significa che quando un Utente viene caricato viene salvato un Proxy del Ruolo (un Ruolo "finto")
            // e solo quando si fa Utente.getSetRuoli Hibernate carica anche i Ruoli SE la sessione è ancora aperta.
            // Se la sessione è chiusa e si cercano i Ruolo viene lanciata un'eccezione.
            // Nel nostro caso usiamo Eager poichè, quando carichiamo i ruoli, abbiamo bisogno di caricare anche i ruoli
            // per evitare che la sessione venga chiusa senza caricarli.
            fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE // In questo modo quando salviamo un utente viene anche salvato il ruolo (aggiornando la tabella di mezzo)
    )
    @JoinTable(
            name = "utente_ruolo",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idRuolo")
    )
    private Set<Ruolo> setRuoli = new HashSet<>();
}
