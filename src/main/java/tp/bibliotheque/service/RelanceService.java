package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tp.bibliotheque.dto.RelanceResponse;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmpruntRepository;

@Service
@RequiredArgsConstructor
public class RelanceService {

    private final EmpruntRepository empruntRepository;
    private final EmpruntService empruntService;

    @Transactional
    public RelanceResponse envoyerRelance(Long empruntId) {
        empruntService.updateRetards();

        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new NotFoundException("Emprunt introuvable : " + empruntId));

        if (emprunt.getStatut() != StatutEmprunt.EN_RETARD) {
            throw new BusinessException("La relance ne peut être envoyée que pour un emprunt en retard.");
        }

        String email = emprunt.getUtilisateur().getEmail();
        String prenom = emprunt.getUtilisateur().getPrenom();
        String titre = emprunt.getExemplaire().getRessource().getTitre();
        var dateRetourPrevue = emprunt.getDateRetourPrevue();

        System.out.println("=== RELANCE SIMULÉE ===");
        System.out.println("À : " + email);
        System.out.println("Sujet : Relance de prêt en retard");
        System.out.println("Message :");
        System.out.println("Bonjour " + prenom + ",");
        System.out.println();
        System.out.println("Votre emprunt pour la ressource '" + titre
                + "' est en retard depuis le " + dateRetourPrevue + ".");
        System.out.println("Merci d'effectuer le retour dès que possible.");
        System.out.println();
        System.out.println("Bibliothèque universitaire");
        System.out.println("=======================");

        return new RelanceResponse(empruntId, "Relance simulée pour " + email);
    }
}