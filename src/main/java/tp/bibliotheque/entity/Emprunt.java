package tp.bibliotheque.entity;

import jakarta.persistence.*;
import lombok.*;
import tp.bibliotheque.enums.StatutEmprunt;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "emprunts")
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Utilisateur utilisateur;

    @ManyToOne(optional = false)
    private Exemplaire exemplaire;

    @Column(nullable = false)
    private LocalDate dateEmprunt;

    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    private LocalDate dateRetour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEmprunt statut;
}
