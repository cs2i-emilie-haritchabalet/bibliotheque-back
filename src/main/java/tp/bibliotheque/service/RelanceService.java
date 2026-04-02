package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;

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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Relance de prêt en retard");
        message.setText(
                "Bonjour " + prenom + ",\n\n" +
                        "Votre emprunt pour la ressource '" + titre + "' est en retard depuis le " + dateRetourPrevue + ".\n" +
                        "Merci d'effectuer le retour dès que possible.\n\n" +
                        "Bibliothèque universitaire"
        );

        mailSender.send(message);

        return new RelanceResponse(empruntId, "Relance envoyée à " + email);
    }
}