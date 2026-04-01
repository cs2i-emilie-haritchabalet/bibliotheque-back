package tp.bibliotheque.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tp.bibliotheque.dto.RechercheRessourceRequest;
import tp.bibliotheque.dto.RessourceCreateRequest;
import tp.bibliotheque.dto.RessourceResponse;
import tp.bibliotheque.entity.Emplacement;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.Livre;
import tp.bibliotheque.entity.Revue;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.enums.TypeRessource;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.repository.EmplacementRepository;
import tp.bibliotheque.repository.RessourceRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RessourceServiceTest {

    @Mock
    private RessourceRepository ressourceRepository;
    @Mock
    private EmplacementRepository emplacementRepository;
    @Mock
    private RessourceFactory ressourceFactory;

    @InjectMocks
    private RessourceService ressourceService;

    private Emplacement emplacement;
    private Livre livre;

    @BeforeEach
    void setUp() {
        emplacement = Emplacement.builder().id(1L).code("JAVA-B2").libelle("Java").build();
        livre = Livre.builder()
                .id(2L)
                .titre("Spring in Action")
                .auteur("Craig Walls")
                .anneePublication(2022)
                .theme("Spring")
                .cautionExigee(new BigDecimal("35.00"))
                .emplacement(emplacement)
                .isbn("9781617298691")
                .exemplaires(List.of(
                        Exemplaire.builder().id(10L).codeBarres("EX-004").statut(StatutExemplaire.DISPONIBLE).build()
                ))
                .build();
    }

    @Test
    void rechercheSimple_devraitRetournerLesRessourcesParTitre() {
        when(ressourceRepository.findByTitreContainingIgnoreCase("Spring")).thenReturn(List.of(livre));

        List<RessourceResponse> responses = ressourceService.rechercheSimple("Spring");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).titre()).isEqualTo("Spring in Action");
    }

    @Test
    void rechercheSimple_devraitRetournerListeVideQuandAucunResultat() {
        when(ressourceRepository.findByTitreContainingIgnoreCase("Introuvable")).thenReturn(List.of());

        assertThat(ressourceService.rechercheSimple("Introuvable")).isEmpty();
    }

    @Test
    void rechercheAvancee_devraitFiltrerParAuteurAnneeTheme() {
        RechercheRessourceRequest request = new RechercheRessourceRequest("Spring", "Craig", 2022, "Spring");
        when(ressourceRepository.rechercheAvancee("Spring", "Craig", 2022, "Spring")).thenReturn(List.of(livre));

        List<RessourceResponse> responses = ressourceService.rechercheAvancee(request);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).referenceSpecifique()).isEqualTo("ISBN: 9781617298691");
    }

    @Test
    void detail_devraitRetournerLaRessourceDemandee() {
        when(ressourceRepository.findById(2L)).thenReturn(Optional.of(livre));

        RessourceResponse response = ressourceService.detail(2L);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.emplacement()).isEqualTo("JAVA-B2 - Java");
    }

    @Test
    void detail_devraitLeverNotFoundQuandRessourceInexistante() {
        when(ressourceRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ressourceService.detail(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ressource introuvable : 404");
    }

    @Test
    void creer_devraitCreerUneRessourceAvecEmplacementExistant() {
        RessourceCreateRequest request = new RessourceCreateRequest(
                TypeRessource.LIVRE,
                "Spring in Action",
                "Craig Walls",
                2022,
                "Spring",
                new BigDecimal("35.00"),
                "JAVA-B2",
                "Java",
                "9781617298691",
                null,
                1
        );

        when(emplacementRepository.findByCode("JAVA-B2")).thenReturn(Optional.of(emplacement));
        when(ressourceFactory.create(request, emplacement)).thenReturn(livre);
        when(ressourceRepository.save(livre)).thenReturn(livre);

        RessourceResponse response = ressourceService.creer(request);

        assertThat(response.titre()).isEqualTo("Spring in Action");
        verify(emplacementRepository).findByCode("JAVA-B2");
    }

    @Test
    void creer_devraitCreerUneRessourceEtUnNouvelEmplacementSiAbsent() {
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
                1
        );
        Emplacement nouvelEmplacement = Emplacement.builder().id(8L).code("REV-R1").libelle("Revues").build();
        Revue revue = Revue.builder()
                .id(3L)
                .titre("Java Magazine")
                .auteur("Oracle")
                .anneePublication(2024)
                .theme("Java")
                .cautionExigee(new BigDecimal("10.00"))
                .emplacement(nouvelEmplacement)
                .numero(128)
                .build();

        when(emplacementRepository.findByCode("REV-R1")).thenReturn(Optional.empty());
        when(emplacementRepository.save(any(Emplacement.class))).thenReturn(nouvelEmplacement);
        when(ressourceFactory.create(request, nouvelEmplacement)).thenReturn(revue);
        when(ressourceRepository.save(revue)).thenReturn(revue);

        RessourceResponse response = ressourceService.creer(request);

        assertThat(response.type()).isEqualTo("REVUE");
        assertThat(response.emplacement()).isEqualTo("REV-R1 - Revues");
    }
}
