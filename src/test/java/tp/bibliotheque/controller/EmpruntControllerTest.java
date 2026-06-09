package tp.bibliotheque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.service.EmpruntService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpruntController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmpruntControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpruntService empruntService;

    @Test
    void emprunter_devraitRetourner200EtLePayload() throws Exception {
        EmpruntResponse response = new EmpruntResponse(
                1L,
                10L,
                "Alice Dupont",
                20L,
                "Spring en action",
                "SPR-001",
                LocalDate.now(),
                LocalDate.now().plusDays(15),
                null,
                "EN_COURS"
        );

        when(empruntService.emprunter(any())).thenReturn(response);

        mockMvc.perform(post("/api/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "utilisateurId": 10,
                                  "ressourceId": 20
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_COURS"))
                .andExpect(jsonPath("$.codeBarres").value("SPR-001"));
    }

    @Test
    void emprunter_devraitRetourner400QuandPayloadInvalide() throws Exception {
        mockMvc.perform(post("/api/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.utilisateurId").exists())
                .andExpect(jsonPath("$.errors.ressourceId").exists());
    }

    @Test
    void emprunter_devraitRetourner400QuandBusinessException() throws Exception {
        when(empruntService.emprunter(any()))
                .thenThrow(new BusinessException("Caution insuffisante pour emprunter cette ressource."));

        mockMvc.perform(post("/api/emprunts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "utilisateurId": 10,
                                  "ressourceId": 20
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Caution insuffisante pour emprunter cette ressource."));
    }

    @Test
    void retourner_devraitRetourner200QuandRetourValide() throws Exception {
        EmpruntResponse response = new EmpruntResponse(
                1L, 10L, "Alice Dupont", 20L, "Spring en action", "SPR-001",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), LocalDate.now(), "RETOURNE"
        );
        when(empruntService.retourner(1L)).thenReturn(response);

        mockMvc.perform(post("/api/emprunts/retour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "empruntId": 1 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("RETOURNE"));
    }

    @Test
    void retourner_devraitRetourner400QuandDejaRetourne() throws Exception {
        when(empruntService.retourner(1L)).thenThrow(new BusinessException("Cet emprunt a déjà été retourné."));

        mockMvc.perform(post("/api/emprunts/retour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "empruntId": 1 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cet emprunt a déjà été retourné."));
    }

    @Test
    void retourner_devraitRetourner404QuandEmpruntInexistant() throws Exception {
        when(empruntService.retourner(42L)).thenThrow(new NotFoundException("Emprunt introuvable : 42"));

        mockMvc.perform(post("/api/emprunts/retour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "empruntId": 42 }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Emprunt introuvable : 42"));
    }

    @Test
    void listerParUtilisateur_devraitRetourner200EtUneListe() throws Exception {
        when(empruntService.listerParUtilisateur(10L)).thenReturn(List.of(
                new EmpruntResponse(1L, 10L, "Alice Dupont", 20L, "Spring en action", "SPR-001",
                        LocalDate.now(), LocalDate.now().plusDays(15), null, "EN_COURS")
        ));

        mockMvc.perform(get("/api/emprunts/utilisateur/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].utilisateurId").value(10));
    }
}
