package tp.bibliotheque.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ressources")
public abstract class RessourceDocumentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String auteur;

    @Column(nullable = false)
    private Integer anneePublication;

    @Column(nullable = false)
    private String theme;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cautionExigee;

    @ManyToOne(optional = false)
    private Emplacement emplacement;

    @OneToMany(mappedBy = "ressource", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Exemplaire> exemplaires = new ArrayList<>();
}
