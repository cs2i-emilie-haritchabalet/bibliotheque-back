package tp.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tp.bibliotheque.entity.RessourceDocumentaire;

import java.util.List;

public interface RessourceRepository extends JpaRepository<RessourceDocumentaire, Long> {

    List<RessourceDocumentaire> findByTitreContainingIgnoreCase(String titre);

    @Query("""
        select r from RessourceDocumentaire r
        where lower(r.titre) like lower(concat('%', coalesce(:titre, ''), '%'))
          and lower(r.auteur) like lower(concat('%', coalesce(:auteur, ''), '%'))
          and (:annee is null or r.anneePublication = :annee)
          and lower(r.theme) like lower(concat('%', coalesce(:theme, ''), '%'))
    """)
    List<RessourceDocumentaire> rechercheAvancee(
            @Param("titre") String titre,
            @Param("auteur") String auteur,
            @Param("annee") Integer annee,
            @Param("theme") String theme
    );
}
