package tp.bibliotheque.dto;

import java.math.BigDecimal;
import java.util.List;

public record RessourceResponse(
        Long id,
        String type,
        String titre,
        String auteur,
        Integer anneePublication,
        String theme,
        BigDecimal cautionExigee,
        String emplacement,
        String referenceSpecifique,
        List<ExemplaireResponse> exemplaires
) {
}
