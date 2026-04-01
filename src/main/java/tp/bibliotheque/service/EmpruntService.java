package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tp.bibliotheque.dto.EmpruntCreateRequest;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.RessourceDocumentaire;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmpruntRepository;
import tp.bibliotheque.repository.ExemplaireRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpruntService {

    private static final int DUREE_PRET_JOURS = 15;

    private final EmpruntRepository empruntRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final UtilisateurService utilisateurService;
    private final RessourceService ressourceService;

    @Transactional
    public EmpruntResponse emprunter(EmpruntCreateRequest request) {
        Utilisateur utilisateur = utilisateurService.getEntity(request.utilisateurId());
        RessourceDocumentaire ressource = ressourceService.getEntity(request.ressourceId());

        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new BusinessException("L'utilisateur doit être actif pour emprunter.");
        }
        if (utilisateur.getCautionDisponible().compareTo(ressource.getCautionExigee()) < 0) {
            throw new BusinessException("Caution insuffisante pour emprunter cette ressource.");
        }
        if (!empruntRepository.findRetardsByUtilisateur(utilisateur.getId()).isEmpty()) {
            throw new BusinessException("Un utilisateur avec un emprunt en retard ne peut pas emprunter une nouvelle ressource.");
        }
        empruntRepository.findEmpruntActifByUtilisateurEtRessource(utilisateur.getId(), ressource.getId())
                .ifPresent(e -> {
                    throw new BusinessException("L'utilisateur possède déjà un exemplaire de cette ressource.");
                });

        Exemplaire exemplaire = exemplaireRepository.findByRessourceIdAndStatut(ressource.getId(), StatutExemplaire.DISPONIBLE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("Aucun exemplaire disponible pour cette ressource."));

        exemplaire.setStatut(StatutExemplaire.EMPRUNTE);

        Emprunt emprunt = Emprunt.builder()
                .utilisateur(utilisateur)
                .exemplaire(exemplaire)
                .dateEmprunt(LocalDate.now())
                .dateRetourPrevue(LocalDate.now().plusDays(DUREE_PRET_JOURS))
                .statut(StatutEmprunt.EN_COURS)
                .build();

        return toResponse(empruntRepository.save(emprunt));
    }

    @Transactional
    public EmpruntResponse retourner(Long empruntId) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new NotFoundException("Emprunt introuvable : " + empruntId));

        if (emprunt.getStatut() == StatutEmprunt.RETOURNE) {
            throw new BusinessException("Cet emprunt a déjà été retourné.");
        }

        emprunt.setDateRetour(LocalDate.now());
        emprunt.setStatut(StatutEmprunt.RETOURNE);
        emprunt.getExemplaire().setStatut(StatutExemplaire.DISPONIBLE);

        return toResponse(empruntRepository.save(emprunt));
    }

    @Transactional
    public List<EmpruntResponse> listerParUtilisateur(Long utilisateurId) {
        updateRetards();
        return empruntRepository.findByUtilisateurId(utilisateurId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public List<EmpruntResponse> listerRetards() {
        updateRetards();
        return empruntRepository.findByStatut(StatutEmprunt.EN_RETARD).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void updateRetards() {
        List<Emprunt> enRetard = empruntRepository.findOverdues(LocalDate.now());
        enRetard.forEach(emprunt -> emprunt.setStatut(StatutEmprunt.EN_RETARD));
    }

    public EmpruntResponse toResponse(Emprunt emprunt) {
        return new EmpruntResponse(
                emprunt.getId(),
                emprunt.getUtilisateur().getId(),
                emprunt.getUtilisateur().getPrenom() + " " + emprunt.getUtilisateur().getNom(),
                emprunt.getExemplaire().getRessource().getId(),
                emprunt.getExemplaire().getRessource().getTitre(),
                emprunt.getExemplaire().getCodeBarres(),
                emprunt.getDateEmprunt(),
                emprunt.getDateRetourPrevue(),
                emprunt.getDateRetour(),
                emprunt.getStatut().name()
        );
    }
}
