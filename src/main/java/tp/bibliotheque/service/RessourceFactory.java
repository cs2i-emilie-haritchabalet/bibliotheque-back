package tp.bibliotheque.service;

import org.springframework.stereotype.Component;
import tp.bibliotheque.dto.RessourceCreateRequest;
import tp.bibliotheque.entity.*;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.enums.TypeRessource;
import tp.bibliotheque.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RessourceFactory {

    public RessourceDocumentaire create(RessourceCreateRequest request, Emplacement emplacement) {
        RessourceDocumentaire ressource = switch (request.type()) {
            case LIVRE -> buildLivre(request, emplacement);
            case REVUE -> buildRevue(request, emplacement);
        };

        List<Exemplaire> exemplaires = new ArrayList<>();
        for (int i = 0; i < request.nombreExemplaires(); i++) {
            exemplaires.add(Exemplaire.builder()
                    .codeBarres(generateBarcode(request.type()))
                    .statut(StatutExemplaire.DISPONIBLE)
                    .ressource(ressource)
                    .build());
        }
        ressource.setExemplaires(exemplaires);
        return ressource;
    }

    private Livre buildLivre(RessourceCreateRequest request, Emplacement emplacement) {
        if (request.isbn() == null || request.isbn().isBlank()) {
            throw new BusinessException("L'ISBN est obligatoire pour un livre.");
        }
        return Livre.builder()
                .titre(request.titre())
                .auteur(request.auteur())
                .anneePublication(request.anneePublication())
                .theme(request.theme())
                .cautionExigee(request.cautionExigee())
                .emplacement(emplacement)
                .isbn(request.isbn())
                .build();
    }

    private Revue buildRevue(RessourceCreateRequest request, Emplacement emplacement) {
        if (request.numero() == null) {
            throw new BusinessException("Le numéro est obligatoire pour une revue.");
        }
        return Revue.builder()
                .titre(request.titre())
                .auteur(request.auteur())
                .anneePublication(request.anneePublication())
                .theme(request.theme())
                .cautionExigee(request.cautionExigee())
                .emplacement(emplacement)
                .numero(request.numero())
                .build();
    }

    private String generateBarcode(TypeRessource type) {
        return type.name().substring(0, 3) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
