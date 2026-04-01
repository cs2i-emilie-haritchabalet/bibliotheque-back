package tp.bibliotheque.dto;

import java.time.LocalDate;

public record EmpruntResponse(
        Long id,
        Long utilisateurId,
        String utilisateur,
        Long ressourceId,
        String ressource,
        String codeBarres,
        LocalDate dateEmprunt,
        LocalDate dateRetourPrevue,
        LocalDate dateRetour,
        String statut
) {
}
