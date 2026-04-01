package tp.bibliotheque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tp.bibliotheque.enums.TypeUtilisateur;

import java.math.BigDecimal;

public record UtilisateurCreateRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @Email @NotBlank String email,
        @NotBlank String motDePasse,
        @NotNull TypeUtilisateur type,
        @NotNull @DecimalMin("0.0") BigDecimal cautionDisponible
) {
}
