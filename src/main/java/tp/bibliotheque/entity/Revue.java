package tp.bibliotheque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Revue extends RessourceDocumentaire {

    @Column(nullable = false)
    private Integer numero;
}
