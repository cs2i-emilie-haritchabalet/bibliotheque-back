package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tp.bibliotheque.dto.ExemplaireResponse;
import tp.bibliotheque.dto.RechercheRessourceRequest;
import tp.bibliotheque.dto.RessourceCreateRequest;
import tp.bibliotheque.dto.RessourceResponse;
import tp.bibliotheque.entity.*;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmplacementRepository;
import tp.bibliotheque.repository.RessourceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final RessourceRepository ressourceRepository;
    private final EmplacementRepository emplacementRepository;
    private final RessourceFactory ressourceFactory;

    @Transactional
    public RessourceResponse creer(RessourceCreateRequest request) {
        Emplacement emplacement = emplacementRepository.findByCode(request.emplacementCode())
                .orElseGet(() -> emplacementRepository.save(Emplacement.builder()
                        .code(request.emplacementCode())
                        .libelle(request.emplacementLibelle())
                        .build()));

        RessourceDocumentaire ressource = ressourceFactory.create(request, emplacement);
        return toResponse(ressourceRepository.save(ressource));
    }

    @Transactional(readOnly = true)
    public List<RessourceResponse> rechercheSimple(String titre) {
        return ressourceRepository.findByTitreContainingIgnoreCase(titre).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RessourceResponse> rechercheAvancee(RechercheRessourceRequest request) {
        return ressourceRepository.rechercheAvancee(
                blankToNull(request.titre()),
                blankToNull(request.auteur()),
                request.anneePublication(),
                blankToNull(request.theme())
        ).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RessourceDocumentaire getEntity(Long id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ressource introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public RessourceResponse detail(Long id) {
        return toResponse(getEntity(id));
    }

    public RessourceResponse toResponse(RessourceDocumentaire ressource) {
        String type = ressource instanceof Livre ? "LIVRE" : "REVUE";
        String ref = ressource instanceof Livre livre ? "ISBN: " + livre.getIsbn() : "N° revue: " + ((Revue) ressource).getNumero();

        return new RessourceResponse(
                ressource.getId(),
                type,
                ressource.getTitre(),
                ressource.getAuteur(),
                ressource.getAnneePublication(),
                ressource.getTheme(),
                ressource.getCautionExigee(),
                ressource.getEmplacement().getCode() + " - " + ressource.getEmplacement().getLibelle(),
                ref,
                ressource.getExemplaires().stream()
                        .map(e -> new ExemplaireResponse(e.getId(), e.getCodeBarres(), e.getStatut().name()))
                        .toList()
        );
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
