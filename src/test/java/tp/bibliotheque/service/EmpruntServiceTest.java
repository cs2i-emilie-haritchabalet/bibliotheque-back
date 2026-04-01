package tp.bibliotheque.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tp.bibliotheque.dto.EmpruntCreateRequest;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.entity.Emplacement;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.Livre;
import tp.bibliotheque.entity.RessourceDocumentaire;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.enums.TypeUtilisateur;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmpruntRepository;
import tp.bibliotheque.repository.ExemplaireRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpruntServiceTest {

    @Mock
    private EmpruntRepository empruntRepository;
    @Mock
    private ExemplaireRepository exemplaireRepository;
    @Mock
    private UtilisateurService utilisateurService;
    @Mock
    private RessourceService ressourceService;

    @InjectMocks
    private EmpruntService empruntService;

    private Utilisateur utilisateur;
    private Livre ressource;
    private Exemplaire exemplaireDisponible;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .id(10L)
                .nom("Dupont")
                .prenom("Alice")
                .email("alice@univ.test")
                .motDePasse("encoded")
                .role(Role.USER)
                .type(TypeUtilisateur.ETUDIANT)
                .cautionDisponible(new BigDecimal("100.00"))
                .actif(true)
                .build();

        Emplacement emplacement = Emplacement.builder().id(1L).code("INFO-A1").libelle("Informatique").build();
        ressource = Livre.builder()
                .id(20L)
                .titre("Spring en action")
                .auteur("Craig Walls")
                .anneePublication(2022)
                .theme("Spring")
                .cautionExigee(new BigDecimal("20.00"))
                .emplacement(emplacement)
                .isbn("9780000000000")
                .build();

        exemplaireDisponible = Exemplaire.builder()
                .id(30L)
                .codeBarres("SPR-001")
                .statut(StatutExemplaire.DISPONIBLE)
                .ressource(ressource)
                .build();
    }

    @Test
    void emprunter_devraitCreerEmpruntQuandConditionsValides() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);
        when(empruntRepository.findRetardsByUtilisateur(10L)).thenReturn(List.of());
        when(empruntRepository.findEmpruntActifByUtilisateurEtRessource(10L, 20L)).thenReturn(Optional.empty());
        when(exemplaireRepository.findByRessourceIdAndStatut(20L, StatutExemplaire.DISPONIBLE))
                .thenReturn(List.of(exemplaireDisponible));
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(invocation -> {
            Emprunt emprunt = invocation.getArgument(0);
            emprunt.setId(1L);
            return emprunt;
        });

        EmpruntResponse response = empruntService.emprunter(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.utilisateurId()).isEqualTo(10L);
        assertThat(response.ressourceId()).isEqualTo(20L);
        assertThat(response.codeBarres()).isEqualTo("SPR-001");
        assertThat(response.statut()).isEqualTo("EN_COURS");
        assertThat(response.dateEmprunt()).isEqualTo(LocalDate.now());
        assertThat(response.dateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(15));
        assertThat(exemplaireDisponible.getStatut()).isEqualTo(StatutExemplaire.EMPRUNTE);
    }

    @Test
    void emprunter_devraitRefuserQuandUtilisateurInactif() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);
        utilisateur.setActif(false);

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);

        assertThatThrownBy(() -> empruntService.emprunter(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'utilisateur doit être actif pour emprunter.");

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void emprunter_devraitRefuserQuandCautionInsuffisante() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);
        utilisateur.setCautionDisponible(new BigDecimal("5.00"));

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);

        assertThatThrownBy(() -> empruntService.emprunter(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Caution insuffisante pour emprunter cette ressource.");
    }

    @Test
    void emprunter_devraitRefuserQuandUtilisateurADuRetard() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);
        Emprunt retard = Emprunt.builder().id(99L).statut(StatutEmprunt.EN_RETARD).build();

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);
        when(empruntRepository.findRetardsByUtilisateur(10L)).thenReturn(List.of(retard));

        assertThatThrownBy(() -> empruntService.emprunter(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un utilisateur avec un emprunt en retard ne peut pas emprunter une nouvelle ressource.");
    }

    @Test
    void emprunter_devraitRefuserQuandRessourceDejaEmpruntee() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);
        Emprunt empruntActif = Emprunt.builder().id(70L).statut(StatutEmprunt.EN_COURS).build();

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);
        when(empruntRepository.findRetardsByUtilisateur(10L)).thenReturn(List.of());
        when(empruntRepository.findEmpruntActifByUtilisateurEtRessource(10L, 20L))
                .thenReturn(Optional.of(empruntActif));

        assertThatThrownBy(() -> empruntService.emprunter(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'utilisateur possède déjà un exemplaire de cette ressource.");
    }

    @Test
    void emprunter_devraitRefuserQuandAucunExemplaireDisponible() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);
        when(empruntRepository.findRetardsByUtilisateur(10L)).thenReturn(List.of());
        when(empruntRepository.findEmpruntActifByUtilisateurEtRessource(10L, 20L)).thenReturn(Optional.empty());
        when(exemplaireRepository.findByRessourceIdAndStatut(20L, StatutExemplaire.DISPONIBLE)).thenReturn(List.of());

        assertThatThrownBy(() -> empruntService.emprunter(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Aucun exemplaire disponible pour cette ressource.");
    }

    @Test
    void emprunter_devraitFixerDateRetourPrevueA15Jours() {
        EmpruntCreateRequest request = new EmpruntCreateRequest(10L, 20L);

        when(utilisateurService.getEntity(10L)).thenReturn(utilisateur);
        when(ressourceService.getEntity(20L)).thenReturn(ressource);
        when(empruntRepository.findRetardsByUtilisateur(10L)).thenReturn(List.of());
        when(empruntRepository.findEmpruntActifByUtilisateurEtRessource(10L, 20L)).thenReturn(Optional.empty());
        when(exemplaireRepository.findByRessourceIdAndStatut(20L, StatutExemplaire.DISPONIBLE))
                .thenReturn(List.of(exemplaireDisponible));
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        empruntService.emprunter(request);

        ArgumentCaptor<Emprunt> captor = ArgumentCaptor.forClass(Emprunt.class);
        verify(empruntRepository).save(captor.capture());
        assertThat(captor.getValue().getDateRetourPrevue()).isEqualTo(LocalDate.now().plusDays(15));
    }

    @Test
    void retourner_devraitPasserEmpruntARetourneEtExemplaireADisponible() {
        Emprunt emprunt = Emprunt.builder()
                .id(1L)
                .utilisateur(utilisateur)
                .exemplaire(Exemplaire.builder()
                        .id(30L)
                        .codeBarres("SPR-001")
                        .statut(StatutExemplaire.EMPRUNTE)
                        .ressource(ressource)
                        .build())
                .dateEmprunt(LocalDate.now().minusDays(5))
                .dateRetourPrevue(LocalDate.now().plusDays(10))
                .statut(StatutEmprunt.EN_COURS)
                .build();

        when(empruntRepository.findById(1L)).thenReturn(Optional.of(emprunt));
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpruntResponse response = empruntService.retourner(1L);

        assertThat(response.statut()).isEqualTo("RETOURNE");
        assertThat(response.dateRetour()).isEqualTo(LocalDate.now());
        assertThat(emprunt.getExemplaire().getStatut()).isEqualTo(StatutExemplaire.DISPONIBLE);
    }

    @Test
    void retourner_devraitRefuserQuandEmpruntDejaRetourne() {
        Emprunt emprunt = Emprunt.builder().id(1L).statut(StatutEmprunt.RETOURNE).build();
        when(empruntRepository.findById(1L)).thenReturn(Optional.of(emprunt));

        assertThatThrownBy(() -> empruntService.retourner(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cet emprunt a déjà été retourné.");
    }

    @Test
    void retourner_devraitLeverNotFoundQuandEmpruntInexistant() {
        when(empruntRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.retourner(42L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Emprunt introuvable : 42");
    }

    @Test
    void updateRetards_devraitPasserLesEmpruntsEchusEnRetard() {
        Emprunt overdue1 = Emprunt.builder().id(1L).statut(StatutEmprunt.EN_COURS).build();
        Emprunt overdue2 = Emprunt.builder().id(2L).statut(StatutEmprunt.EN_COURS).build();
        when(empruntRepository.findOverdues(LocalDate.now())).thenReturn(List.of(overdue1, overdue2));

        empruntService.updateRetards();

        assertThat(overdue1.getStatut()).isEqualTo(StatutEmprunt.EN_RETARD);
        assertThat(overdue2.getStatut()).isEqualTo(StatutEmprunt.EN_RETARD);
    }

    @Test
    void listerParUtilisateur_devraitMettreAJourLesRetardsAvantRetour() {
        Emprunt emprunt = Emprunt.builder()
                .id(1L)
                .utilisateur(utilisateur)
                .exemplaire(Exemplaire.builder().id(30L).codeBarres("SPR-001").ressource(ressource).build())
                .dateEmprunt(LocalDate.now().minusDays(1))
                .dateRetourPrevue(LocalDate.now().plusDays(14))
                .statut(StatutEmprunt.EN_COURS)
                .build();

        when(empruntRepository.findOverdues(LocalDate.now())).thenReturn(List.of());
        when(empruntRepository.findByUtilisateurId(10L)).thenReturn(List.of(emprunt));

        List<EmpruntResponse> responses = empruntService.listerParUtilisateur(10L);

        verify(empruntRepository).findOverdues(LocalDate.now());
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).utilisateurId()).isEqualTo(10L);
    }

    @Test
    void listerRetards_devraitMettreAJourLesRetardsAvantRetour() {
        Emprunt emprunt = Emprunt.builder()
                .id(2L)
                .utilisateur(utilisateur)
                .exemplaire(Exemplaire.builder().id(30L).codeBarres("SPR-001").ressource(ressource).build())
                .dateEmprunt(LocalDate.now().minusDays(20))
                .dateRetourPrevue(LocalDate.now().minusDays(5))
                .statut(StatutEmprunt.EN_RETARD)
                .build();

        when(empruntRepository.findOverdues(LocalDate.now())).thenReturn(List.of(emprunt));
        when(empruntRepository.findByStatut(StatutEmprunt.EN_RETARD)).thenReturn(List.of(emprunt));

        List<EmpruntResponse> responses = empruntService.listerRetards();

        verify(empruntRepository).findOverdues(LocalDate.now());
        verify(empruntRepository).findByStatut(StatutEmprunt.EN_RETARD);
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).statut()).isEqualTo("EN_RETARD");
    }
}
