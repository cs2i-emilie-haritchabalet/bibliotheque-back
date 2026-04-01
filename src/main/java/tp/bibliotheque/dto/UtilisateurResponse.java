package tp.bibliotheque.dto;

import java.math.BigDecimal;

public record UtilisateurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role,
        String type,
        BigDecimal cautionDisponible,
        Boolean actif
) {
}
