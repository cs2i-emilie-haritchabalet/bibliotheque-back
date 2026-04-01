package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tp.bibliotheque.dto.UtilisateurCreateRequest;
import tp.bibliotheque.dto.UtilisateurResponse;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.TypeUtilisateur;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.UtilisateurRepository;
import java.math.BigDecimal;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UtilisateurResponse creer(UtilisateurCreateRequest request) {
        utilisateurRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Un utilisateur avec cet email existe déjà.");
        });

        Role role = request.type() == TypeUtilisateur.BIBLIOTHECAIRE ? Role.BIBLIOTHECAIRE : Role.USER;

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .type(request.type())
                .role(role)
                .cautionDisponible(request.cautionDisponible())
                .actif(true)
                .build();

        return toResponse(utilisateurRepository.save(utilisateur));
    }

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {
        return utilisateurRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Utilisateur getEntity(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + id));
    }

    public UtilisateurResponse toResponse(Utilisateur utilisateur) {
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole().name(),
                utilisateur.getType().name(),
                utilisateur.getCautionDisponible(),
                utilisateur.getActif()
        );
    }



    @Transactional
    public void crediterCaution(Long id, Double montant) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + id));

        BigDecimal cautionActuelle = utilisateur.getCautionDisponible() == null
                ? BigDecimal.ZERO
                : utilisateur.getCautionDisponible();

        BigDecimal montantBD = BigDecimal.valueOf(montant);

        utilisateur.setCautionDisponible(cautionActuelle.add(montantBD));
    }

    @Transactional
    public void debiterCaution(Long id, Double montant) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + id));

        BigDecimal cautionActuelle = utilisateur.getCautionDisponible() == null
                ? BigDecimal.ZERO
                : utilisateur.getCautionDisponible();

        BigDecimal montantBD = BigDecimal.valueOf(montant);

        if (montantBD.compareTo(cautionActuelle) > 0) {
            throw new IllegalArgumentException("Le montant dépasse la caution disponible.");
        }

        utilisateur.setCautionDisponible(cautionActuelle.subtract(montantBD));
    }
}
