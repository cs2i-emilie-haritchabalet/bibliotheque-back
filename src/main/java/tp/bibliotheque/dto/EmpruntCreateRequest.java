package tp.bibliotheque.dto;

import jakarta.validation.constraints.NotNull;

public record EmpruntCreateRequest(
        @NotNull Long utilisateurId,
        @NotNull Long ressourceId
) {
}
