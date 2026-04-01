package tp.bibliotheque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.dto.ExemplaireResponse;
import tp.bibliotheque.dto.RessourceResponse;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.service.RessourceService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RessourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class RessourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RessourceService ressourceService;

    private RessourceResponse buildResponse() {
        return new RessourceResponse(
                2L,
                "LIVRE",
                "Spring in Action",
                "Craig Walls",
                2022,
                "Spring",
                new BigDecimal("35.00"),
                "JAVA-B2 - Java",
                "ISBN: 9781617298691",
                List.of(new ExemplaireResponse(10L, "EX-004", "DISPONIBLE"))
        );
    }

    @Test
    void rechercheSimple_devraitRetourner200EtListe() throws Exception {
        when(ressourceService.rechercheSimple("Spring")).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/ressources/search").param("titre", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value("Spring in Action"));
    }

    @Test
    void rechercheSimple_devraitRetournerListeVideQuandAucunResultat() throws Exception {
        when(ressourceService.rechercheSimple("Introuvable")).thenReturn(List.of());

        mockMvc.perform(get("/api/ressources/search").param("titre", "Introuvable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void rechercheAvancee_devraitRetourner200EtListeFiltree() throws Exception {
        when(ressourceService.rechercheAvancee(any())).thenReturn(List.of(buildResponse()));

        mockMvc.perform(post("/api/ressources/advanced-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titre": "Spring",
                                  "auteur": "Craig",
                                  "anneePublication": 2022,
                                  "theme": "Spring"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].referenceSpecifique").value("ISBN: 9781617298691"));
    }

    @Test
    void detail_devraitRetourner200QuandRessourceExiste() throws Exception {
        when(ressourceService.detail(2L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/ressources/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void detail_devraitRetourner404QuandRessourceInexistante() throws Exception {
        when(ressourceService.detail(404L)).thenThrow(new NotFoundException("Ressource introuvable : 404"));

        mockMvc.perform(get("/api/ressources/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ressource introuvable : 404"));
    }

    @Test
    void creer_devraitRetourner201QuandPayloadValide() throws Exception {
        when(ressourceService.creer(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/ressources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "LIVRE",
                                  "titre": "Spring in Action",
                                  "auteur": "Craig Walls",
                                  "anneePublication": 2022,
                                  "theme": "Spring",
                                  "cautionExigee": 35.00,
                                  "emplacementCode": "JAVA-B2",
                                  "emplacementLibelle": "Java",
                                  "isbn": "9781617298691",
                                  "nombreExemplaires": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("LIVRE"));
    }

    @Test
    void creer_devraitRetourner400QuandPayloadInvalide() throws Exception {
        mockMvc.perform(post("/api/ressources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.type").exists())
                .andExpect(jsonPath("$.errors.titre").exists())
                .andExpect(jsonPath("$.errors.nombreExemplaires").exists());
    }
}
