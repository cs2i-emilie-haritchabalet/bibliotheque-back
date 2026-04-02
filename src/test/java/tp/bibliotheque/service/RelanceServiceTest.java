package tp.bibliotheque.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tp.bibliotheque.dto.RelanceResponse;
import tp.bibliotheque.entity.Emplacement;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.Livre;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.enums.TypeUtilisateur;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmpruntRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelanceServiceTest {

    @Mock
    private EmpruntRepository empruntRepository;

    @Mock
    private EmpruntService empruntService;

    @Mock
    private JavaMailSender mailSender;

    private RelanceService relanceService;

    private Emprunt empruntEnRetard;

    @BeforeEach
    void setUp() {
        relanceService = new RelanceService(empruntRepository, empruntService, mailSender);

        Utilisateur utilisateur = Utilisateur.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Alice")
                .email("alice@univ.test")
                .motDePasse("encoded")
                .role(Role.USER)
                .type(TypeUtilisateur.ETUDIANT)
                .cautionDisponible(new BigDecimal("50.00"))
                .actif(true)
                .build();

        Livre livre = Livre.builder()
                .id(2L)
                .titre("Spring en action")
                .auteur("Craig Walls")
                .anneePublication(2022)
                .theme("Spring")
                .cautionExigee(new BigDecimal("20.00"))
                .emplacement(Emplacement.builder().id(1L).code("A1").libelle("Info").build())
                .isbn("9780000000000")
                .build();

        Exemplaire exemplaire = Exemplaire.builder()
                .id(3L)
                .codeBarres("SPR-001")
                .statut(StatutExemplaire.EMPRUNTE)
                .ressource(livre)
                .build();

        empruntEnRetard = Emprunt.builder()
                .id(5L)
                .utilisateur(utilisateur)
                .exemplaire(exemplaire)
                .dateEmprunt(LocalDate.now().minusDays(20))
                .dateRetourPrevue(LocalDate.now().minusDays(5))
                .statut(StatutEmprunt.EN_RETARD)
                .build();
    }

    @Test
    void envoyerRelance_devraitRetournerUneRelanceQuandEmpruntEstEnRetard() {
        when(empruntRepository.findById(5L)).thenReturn(Optional.of(empruntEnRetard));

        RelanceResponse response = relanceService.envoyerRelance(5L);

        verify(empruntService).updateRetards();
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertThat(response.empruntId()).isEqualTo(5L);
        assertThat(response.message()).contains("alice@univ.test");
    }

    @Test
    void envoyerRelance_devraitLeverNotFoundQuandEmpruntInexistant() {
        when(empruntRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> relanceService.envoyerRelance(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Emprunt introuvable : 99");
    }

    @Test
    void envoyerRelance_devraitRefuserQuandEmpruntPasEnRetard() {
        empruntEnRetard.setStatut(StatutEmprunt.EN_COURS);
        when(empruntRepository.findById(5L)).thenReturn(Optional.of(empruntEnRetard));

        assertThatThrownBy(() -> relanceService.envoyerRelance(5L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La relance ne peut être envoyée que pour un emprunt en retard.");
    }
}