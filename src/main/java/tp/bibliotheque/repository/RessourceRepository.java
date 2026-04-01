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
        where (:titre is null or lower(r.titre) like lower(concat('%', :titre, '%')))
          and (:auteur is null or lower(r.auteur) like lower(concat('%', :auteur, '%')))
          and (:annee is null or r.anneePublication = :annee)
          and (:theme is null or lower(r.theme) like lower(concat('%', :theme, '%')))
        """)
    List<RessourceDocumentaire> rechercheAvancee(
            @Param("titre") String titre,
            @Param("auteur") String auteur,
            @Param("annee") Integer annee,
            @Param("theme") String theme
    );
}
