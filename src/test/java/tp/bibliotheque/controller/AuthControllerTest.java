package tp.bibliotheque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.dto.LoginResponse;
import tp.bibliotheque.exception.ApiExceptionHandler;
import tp.bibliotheque.exception.NotFoundException;
import tp.bibliotheque.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void login_devraitRetourner200QuandIdentifiantsValides() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                1L,
                "Alice Dupont",
                "alice@univ.test",
                "USER",
                "ETUDIANT"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@univ.test",
                                  "motDePasse": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@univ.test"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_devraitRetourner400QuandPayloadInvalide() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "motDePasse": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.motDePasse").exists());
    }

    @Test
    void login_devraitRetourner404QuandUtilisateurIntrouvable() throws Exception {
        when(authService.login(any())).thenThrow(new NotFoundException("Utilisateur introuvable."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ghost@univ.test",
                                  "motDePasse": "secret"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utilisateur introuvable."));
    }
}
