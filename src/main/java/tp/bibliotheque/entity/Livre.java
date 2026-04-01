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
public class Livre extends RessourceDocumentaire {

    @Column(nullable = false, unique = true)
    private String isbn;
}
