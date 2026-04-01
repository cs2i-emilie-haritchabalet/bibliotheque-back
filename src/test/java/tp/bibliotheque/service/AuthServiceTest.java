package tp.bibliotheque.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tp.bibliotheque.dto.LoginRequest;
import tp.bibliotheque.dto.LoginResponse;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.TypeUtilisateur;
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
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_devraitRetournerLeProfilQuandAuthentificationValide() {
        LoginRequest request = new LoginRequest("alice@univ.test", "secret");
        Authentication authentication = new UsernamePasswordAuthenticationToken("alice@univ.test", "secret");
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

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(utilisateurRepository.findByEmail("alice@univ.test")).thenReturn(Optional.of(utilisateur));

        LoginResponse response = authService.login(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nomComplet()).isEqualTo("Alice Dupont");
        assertThat(response.email()).isEqualTo("alice@univ.test");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.type()).isEqualTo("ETUDIANT");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_devraitLeverNotFoundQuandUtilisateurAuthentifieIntrouvable() {
        LoginRequest request = new LoginRequest("ghost@univ.test", "secret");
        Authentication authentication = new UsernamePasswordAuthenticationToken("ghost@univ.test", "secret");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(utilisateurRepository.findByEmail("ghost@univ.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur introuvable.");
    }

    @Test
    void login_devraitPropagerErreurAuthentificationQuandMotDePasseIncorrect() {
        LoginRequest request = new LoginRequest("alice@univ.test", "bad");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");
    }
}
