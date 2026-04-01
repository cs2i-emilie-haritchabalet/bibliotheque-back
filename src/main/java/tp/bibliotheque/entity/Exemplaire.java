package tp.bibliotheque.entity;

import jakarta.persistence.*;
import lombok.*;
import tp.bibliotheque.enums.StatutExemplaire;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exemplaires")
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeBarres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutExemplaire statut;

    @ManyToOne(optional = false)
    private RessourceDocumentaire ressource;
}
