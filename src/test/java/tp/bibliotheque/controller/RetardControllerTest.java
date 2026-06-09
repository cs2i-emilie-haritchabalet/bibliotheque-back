package tp.bibliotheque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.dto.EmpruntResponse;
import tp.bibliotheque.dto.RelanceResponse;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.service.EmpruntService;
import tp.bibliotheque.service.RelanceService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RetardController.class)
@AutoConfigureMockMvc(addFilters = false)
class RetardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpruntService empruntService;

    @MockitoBean
    private RelanceService relanceService;

    @Test
    void listerRetards_devraitRetourner200EtLaListe() throws Exception {
        when(empruntService.listerRetards()).thenReturn(List.of(
                new EmpruntResponse(
                        5L,
                        10L,
                        "Alice Dupont",
                        20L,
                        "Spring en action",
                        "SPR-001",
                        LocalDate.now().minusDays(20),
                        LocalDate.now().minusDays(5),
                        null,
                        "EN_RETARD"
                )
        ));

        mockMvc.perform(get("/api/retards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("EN_RETARD"));
    }

    @Test
    void envoyerRelance_devraitRetourner200QuandServiceReussit() throws Exception {
        when(relanceService.envoyerRelance(5L))
                .thenReturn(new RelanceResponse(5L, "Relance envoyée à alice@univ.test"));

        mockMvc.perform(post("/api/retards/5/relance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empruntId").value(5))
                .andExpect(jsonPath("$.message").value("Relance envoyée à alice@univ.test"));
    }

    @Test
    void envoyerRelance_devraitPropager400QuandEmpruntPasEnRetard() throws Exception {
        doThrow(new BusinessException("La relance ne peut être envoyée que pour un emprunt en retard."))
                .when(relanceService).envoyerRelance(5L);

        mockMvc.perform(post("/api/retards/5/relance"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La relance ne peut être envoyée que pour un emprunt en retard."));
    }

    @Test
    void envoyerRelance_devraitRetourner404QuandEmpruntInexistant() throws Exception {
        doThrow(new NotFoundException("Emprunt introuvable : 404"))
                .when(relanceService).envoyerRelance(404L);

        mockMvc.perform(post("/api/retards/404/relance"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Emprunt introuvable : 404"));
    }
}
