package tp.bibliotheque.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tp.bibliotheque.entity.*;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.repository.EmpruntRepository;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
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

    @MockBean
    private EmpruntRepository empruntRepository;

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
    @WithMockUser(username = "alice", roles = {"ETUDIANT"})
    void creerUtilisateur_devraitRefuserAvecUtilisateurSimple() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
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
    @WithMockUser(username = "admin", roles = {"BIBLIOTHECAIRE"})
    void creerUtilisateur_devraitAutoriserLeBibliothecaire() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
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
    @WithMockUser(username = "alice", roles = {"ETUDIANT"})
    void creerRessource_devraitRefuserAvecUtilisateurSimple() throws Exception {
        mockMvc.perform(post("/api/ressources")
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
    @WithMockUser(username = "admin", roles = {"BIBLIOTHECAIRE"})
    void envoyerRelance_devraitAutoriserLeBibliothecaire() throws Exception {

        // --- MOCK MINIMAL POUR EVITER 404 ---
        Utilisateur user = new Utilisateur();
        user.setId(3L);
        user.setNom("Durand");
        user.setPrenom("Alice");

        Livre ressource = new Livre();
        ressource.setId(1L);
        ressource.setTitre("Clean Code");

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(2L);
        exemplaire.setRessource(ressource);

        Emprunt emprunt = new Emprunt();
        emprunt.setId(2L);
        emprunt.setUtilisateur(user);
        emprunt.setExemplaire(exemplaire);
        emprunt.setStatut(StatutEmprunt.EN_RETARD);

        given(empruntRepository.findById(2L))
                .willReturn(Optional.of(emprunt));

        mockMvc.perform(post("/api/retards/2/relance"))
                .andExpect(status().isOk());
    }
}
