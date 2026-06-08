package tp.bibliotheque.entity;

import jakarta.persistence.*;
import lombok.*;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.TypeUtilisateur;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeUtilisateur type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cautionDisponible = BigDecimal.ZERO;

    @OneToMany(mappedBy = "utilisateur")
    @Builder.Default
    private List<Emprunt> emprunts = new ArrayList<>();
}
