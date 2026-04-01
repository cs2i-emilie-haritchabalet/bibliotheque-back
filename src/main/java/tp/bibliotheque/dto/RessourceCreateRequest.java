package tp.bibliotheque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tp.bibliotheque.enums.TypeRessource;

import java.math.BigDecimal;

public record RessourceCreateRequest(
        @NotNull TypeRessource type,
        @NotBlank String titre,
        @NotBlank String auteur,
        @NotNull Integer anneePublication,
        @NotBlank String theme,
        @NotNull @DecimalMin("0.0") BigDecimal cautionExigee,
        @NotBlank String emplacementCode,
        @NotBlank String emplacementLibelle,
        String isbn,
        Integer numero,
        @NotNull @Min(1) Integer nombreExemplaires
) {
}
