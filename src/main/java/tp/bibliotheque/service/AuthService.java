package tp.bibliotheque.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tp.bibliotheque.dto.LoginRequest;
import tp.bibliotheque.dto.LoginResponse;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable."));

        return new LoginResponse(
                utilisateur.getId(),
                utilisateur.getPrenom() + " " + utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getRole().name(),
                utilisateur.getType().name()
        );
    }
}
