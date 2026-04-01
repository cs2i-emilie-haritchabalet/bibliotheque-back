package tp.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.bibliotheque.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
}
