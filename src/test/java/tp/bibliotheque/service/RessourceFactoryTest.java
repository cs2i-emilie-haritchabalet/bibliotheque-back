package tp.bibliotheque.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tp.bibliotheque.dto.RessourceCreateRequest;
import tp.bibliotheque.entity.Emplacement;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.Livre;
import tp.bibliotheque.entity.RessourceDocumentaire;
import tp.bibliotheque.entity.Revue;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.enums.TypeRessource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RessourceFactoryTest {

    private RessourceFactory ressourceFactory;
    private Emplacement emplacement;

    @BeforeEach
    void setUp() {
        ressourceFactory = new RessourceFactory();
        emplacement = Emplacement.builder().id(1L).code("INFO-A1").libelle("Informatique").build();
    }

    @Test
    void create_devraitCreerUnLivreAvecNombreExemplairesDemande() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.LIVRE,
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programmation",
                new BigDecimal("25.00"),
                "INFO-A1",
                "Informatique",
                "9780132350884",
                null,
                2
        );

        RessourceDocumentaire ressource = ressourceFactory.create(request, emplacement);

        assertThat(ressource).isInstanceOf(Livre.class);
        assertThat(ressource.getExemplaires()).hasSize(2);
        assertThat(((Livre) ressource).getIsbn()).isEqualTo("9780132350884");
        assertThat(ressource.getExemplaires()).extracting(Exemplaire::getRessource).containsOnly(ressource);
    }

    @Test
    void create_devraitCreerUneRevueAvecNombreExemplairesDemande() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.REVUE,
                "Java Magazine",
                "Oracle",
                2024,
                "Java",
                new BigDecimal("10.00"),
                "REV-R1",
                "Revues",
                null,
                128,
                3
        );

        RessourceDocumentaire ressource = ressourceFactory.create(request, emplacement);

        assertThat(ressource).isInstanceOf(Revue.class);
        assertThat(((Revue) ressource).getNumero()).isEqualTo(128);
        assertThat(ressource.getExemplaires()).hasSize(3);
    }

    @Test
    void create_devraitGenererDesCodesBarresUniques() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.LIVRE,
                "Spring in Action",
                "Craig Walls",
                2022,
                "Spring",
                new BigDecimal("30.00"),
                "JAVA-B2",
                "Java",
                "9781617298691",
                null,
                4
        );

        RessourceDocumentaire ressource = ressourceFactory.create(request, emplacement);
        List<String> codes = ressource.getExemplaires().stream().map(Exemplaire::getCodeBarres).toList();

        assertThat(codes).hasSize(4);
        assertThat(codes).doesNotHaveDuplicates();
        assertThat(codes).allMatch(code -> code.startsWith("LIV-"));
    }

    @Test
    void create_devraitMettreTousLesExemplairesSurLeMemeEmplacement() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.REVUE,
                "Tech Review",
                "Editor",
                2023,
                "Tech",
                new BigDecimal("5.00"),
                "REV-R1",
                "Revues",
                null,
                12,
                2
        );

        RessourceDocumentaire ressource = ressourceFactory.create(request, emplacement);

        assertThat(ressource.getExemplaires()).allSatisfy(ex -> assertThat(ex.getRessource().getEmplacement()).isEqualTo(emplacement));
    }

    @Test
    void create_devraitRefuserLivreSansIsbn() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.LIVRE,
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programmation",
                new BigDecimal("25.00"),
                "INFO-A1",
                "Informatique",
                " ",
                null,
                1
        );

        assertThatThrownBy(() -> ressourceFactory.create(request, emplacement))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'ISBN est obligatoire pour un livre.");
    }

    @Test
    void create_devraitRefuserRevueSansNumero() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.REVUE,
                "Java Magazine",
                "Oracle",
                2024,
                "Java",
                new BigDecimal("10.00"),
                "REV-R1",
                "Revues",
                null,
                null,
                1
        );

        assertThatThrownBy(() -> ressourceFactory.create(request, emplacement))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le numéro est obligatoire pour une revue.");
    }
}
