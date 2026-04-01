package tp.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.enums.StatutEmprunt;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByUtilisateurId(Long utilisateurId);
    List<Emprunt> findByStatut(StatutEmprunt statut);
    Optional<Emprunt> findByExemplaireIdAndStatut(Long exemplaireId, StatutEmprunt statut);

    @Query("""
            select e from Emprunt e
            where e.utilisateur.id = :utilisateurId
              and e.exemplaire.ressource.id = :ressourceId
              and e.statut in (tp.bibliotheque.enums.StatutEmprunt.EN_COURS, tp.bibliotheque.enums.StatutEmprunt.EN_RETARD)
            """)
    Optional<Emprunt> findEmpruntActifByUtilisateurEtRessource(Long utilisateurId, Long ressourceId);

    @Query("""
            select e from Emprunt e
            where e.utilisateur.id = :utilisateurId
              and e.statut = tp.bibliotheque.enums.StatutEmprunt.EN_RETARD
            """)
    List<Emprunt> findRetardsByUtilisateur(Long utilisateurId);

    @Query("""
            select e from Emprunt e
            where e.dateRetour is null and e.dateRetourPrevue < :today and e.statut <> tp.bibliotheque.enums.StatutEmprunt.RETOURNE
            """)
    List<Emprunt> findOverdues(LocalDate today);
}
