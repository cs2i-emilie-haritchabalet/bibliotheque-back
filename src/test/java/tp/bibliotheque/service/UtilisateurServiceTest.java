package tp.bibliotheque.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tp.bibliotheque.dto.UtilisateurCreateRequest;
import tp.bibliotheque.dto.UtilisateurResponse;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.TypeUtilisateur;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.UtilisateurRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private UtilisateurCreateRequest etudiantRequest;

    @BeforeEach
    void setUp() {
        etudiantRequest = new UtilisateurCreateRequest(
                "Dupont",
                "Alice",
                "alice@univ.test",
                "secret",
                TypeUtilisateur.ETUDIANT,
                new BigDecimal("80.00")
        );
    }

    @Test
    void creer_devraitCreerUtilisateurAvecRoleUserQuandTypeNonBibliothecaire() {
        when(utilisateurRepository.findByEmail("alice@univ.test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setId(1L);
            return utilisateur;
        });

        UtilisateurResponse response = utilisateurService.creer(etudiantRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.type()).isEqualTo("ETUDIANT");
        assertThat(response.actif()).isTrue();
    }

    @Test
    void creer_devraitCreerUtilisateurAvecRoleBibliothecaireQuandTypeBibliothecaire() {
        UtilisateurCreateRequest request = new UtilisateurCreateRequest(
                "Admin",
                "Biblio",
                "admin@biblio.fr",
                "admin123",
                TypeUtilisateur.BIBLIOTHECAIRE,
                new BigDecimal("0.00")
        );
        when(utilisateurRepository.findByEmail("admin@biblio.fr")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-admin");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UtilisateurResponse response = utilisateurService.creer(request);

        assertThat(response.role()).isEqualTo("BIBLIOTHECAIRE");
    }

    @Test
    void creer_devraitEncoderLeMotDePasse() {
        when(utilisateurRepository.findByEmail("alice@univ.test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        utilisateurService.creer(etudiantRequest);

        verify(passwordEncoder).encode("secret");
    }

    @Test
    void creer_devraitRefuserQuandEmailExisteDeja() {
        when(utilisateurRepository.findByEmail("alice@univ.test"))
                .thenReturn(Optional.of(Utilisateur.builder().id(1L).email("alice@univ.test").build()));

        assertThatThrownBy(() -> utilisateurService.creer(etudiantRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un utilisateur avec cet email existe déjà.");
    }

    @Test
    void crediterCaution_devraitAjouterLeMontant() {
        Utilisateur utilisateur = Utilisateur.builder().id(1L).cautionDisponible(new BigDecimal("10.00")).build();
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        utilisateurService.crediterCaution(1L, 5.5);

        assertThat(utilisateur.getCautionDisponible()).isEqualByComparingTo("15.5");
    }

    @Test
    void debiterCaution_devraitSoustraireLeMontant() {
        Utilisateur utilisateur = Utilisateur.builder().id(1L).cautionDisponible(new BigDecimal("10.00")).build();
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        utilisateurService.debiterCaution(1L, 4.0);

        assertThat(utilisateur.getCautionDisponible()).isEqualByComparingTo("6.0");
    }

    @Test
    void debiterCaution_devraitRefuserQuandMontantSuperieurAuDisponible() {
        Utilisateur utilisateur = Utilisateur.builder().id(1L).cautionDisponible(new BigDecimal("3.00")).build();
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        assertThatThrownBy(() -> utilisateurService.debiterCaution(1L, 4.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant dépasse la caution disponible.");
    }

    @Test
    void getEntity_devraitLeverNotFoundQuandUtilisateurAbsent() {
        when(utilisateurRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> utilisateurService.getEntity(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur introuvable : 99");
    }
}
