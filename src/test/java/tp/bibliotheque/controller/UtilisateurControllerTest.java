package tp.bibliotheque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.dto.UtilisateurResponse;
import tp.bibliotheque.exception.BusinessException;
import tp.bibliotheque.service.UtilisateurService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilisateurController.class)
@AutoConfigureMockMvc(addFilters = false)
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    private UtilisateurResponse buildResponse() {
        return new UtilisateurResponse(
                1L,
                "Dupont",
                "Alice",
                "alice@univ.test",
                "USER",
                "ETUDIANT",
                new BigDecimal("80.00"),
                true
        );
    }

    @Test
    void creer_devraitRetourner201QuandPayloadValide() throws Exception {
        when(utilisateurService.creer(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Dupont",
                                  "prenom": "Alice",
                                  "email": "alice@univ.test",
                                  "motDePasse": "secret",
                                  "type": "ETUDIANT",
                                  "cautionDisponible": 80.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@univ.test"));
    }

    @Test
    void creer_devraitRetourner400QuandPayloadInvalide() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nom").exists())
                .andExpect(jsonPath("$.errors.prenom").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.type").exists());
    }

    @Test
    void creer_devraitRetourner400QuandEmailDejaUtilise() throws Exception {
        when(utilisateurService.creer(any())).thenThrow(new BusinessException("Un utilisateur avec cet email existe déjà."));

        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Dupont",
                                  "prenom": "Alice",
                                  "email": "alice@univ.test",
                                  "motDePasse": "secret",
                                  "type": "ETUDIANT",
                                  "cautionDisponible": 80.00
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Un utilisateur avec cet email existe déjà."));
    }

    @Test
    void lister_devraitRetourner200EtListe() throws Exception {
        when(utilisateurService.lister()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prenom").value("Alice"));
    }

    @Test
    void crediterCaution_devraitRetourner204QuandPayloadValide() throws Exception {
        mockMvc.perform(patch("/api/utilisateurs/1/credit-caution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "montant": 10.50 }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void debiterCaution_devraitRetourner204QuandPayloadValide() throws Exception {
        mockMvc.perform(patch("/api/utilisateurs/1/debit-caution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "montant": 5.00 }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void debiterCaution_devraitRetourner400QuandMontantInvalide() throws Exception {
        mockMvc.perform(patch("/api/utilisateurs/1/debit-caution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "montant": 0 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.montant").exists());
    }
}
