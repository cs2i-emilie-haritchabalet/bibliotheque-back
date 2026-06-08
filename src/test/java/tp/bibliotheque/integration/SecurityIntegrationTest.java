package tp.bibliotheque.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void creerUtilisateur_devraitRefuserSansAuthentification() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nom": "Test",
                                  "prenom": "User",
                                  "email": "test@univ.test",
                                  "motDePasse": "secret",
                                  "type": "ETUDIANT",
                                  "cautionDisponible": 10.00
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creerUtilisateur_devraitRefuserAvecUtilisateurSimple() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .with(httpBasic("alice@etu.fr", "alice123"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "nom": "Test",
                                  "prenom": "User",
                                  "email": "test2@univ.test",
                                  "motDePasse": "secret",
                                  "type": "ETUDIANT",
                                  "cautionDisponible": 10.00
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void creerUtilisateur_devraitAutoriserLeBibliothecaire() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .with(httpBasic("admin@biblio.fr", "admin123"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "nom": "Nouveau",
                                  "prenom": "Lecteur",
                                  "email": "lecteur@univ.test",
                                  "motDePasse": "secret",
                                  "type": "ETUDIANT",
                                  "cautionDisponible": 10.00
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void creerRessource_devraitRefuserAvecUtilisateurSimple() throws Exception {
        mockMvc.perform(post("/api/ressources")
                        .with(httpBasic("alice@etu.fr", "alice123"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIVRE",
                                  "titre": "Livre sécurité",
                                  "auteur": "Auteur",
                                  "anneePublication": 2024,
                                  "theme": "Test",
                                  "cautionExigee": 5.00,
                                  "emplacementCode": "TEST-A1",
                                  "emplacementLibelle": "Tests",
                                  "isbn": "9781111111111",
                                  "nombreExemplaires": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void envoyerRelance_devraitAutoriserLeBibliothecaire() throws Exception {
        mockMvc.perform(post("/api/retards/2/relance")
                        .with(httpBasic("admin@biblio.fr", "admin123")))
                .andExpect(status().isOk());
    }
}
