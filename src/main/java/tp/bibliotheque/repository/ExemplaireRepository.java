package tp.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.enums.StatutExemplaire;

import java.util.List;
import java.util.Optional;

public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    Optional<Exemplaire> findByCodeBarres(String codeBarres);
    List<Exemplaire> findByRessourceIdAndStatut(Long ressourceId, StatutExemplaire statut);
}
