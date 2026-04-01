package tp.bibliotheque.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tp.bibliotheque.entity.Emplacement;
import tp.bibliotheque.entity.Emprunt;
import tp.bibliotheque.entity.Exemplaire;
import tp.bibliotheque.entity.Livre;
import tp.bibliotheque.entity.Revue;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.enums.Role;
import tp.bibliotheque.enums.StatutEmprunt;
import tp.bibliotheque.enums.StatutExemplaire;
import tp.bibliotheque.enums.TypeUtilisateur;
import tp.bibliotheque.repository.EmplacementRepository;
import tp.bibliotheque.repository.EmpruntRepository;
import tp.bibliotheque.repository.ExemplaireRepository;
import tp.bibliotheque.repository.RessourceRepository;
import tp.bibliotheque.repository.UtilisateurRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init(
            UtilisateurRepository utilisateurRepository,
            EmplacementRepository emplacementRepository,
            RessourceRepository ressourceRepository,
            ExemplaireRepository exemplaireRepository,
            EmpruntRepository empruntRepository
    ) {
        return args -> {

            if (ressourceRepository.count() > 0) {
                return;
            }

            // =========================
            // UTILISATEURS
            // =========================
            Utilisateur admin = utilisateurRepository.save(Utilisateur.builder()
                    .nom("Admin")
                    .prenom("Biblio")
                    .email("admin@biblio.fr")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(Role.BIBLIOTHECAIRE)
                    .type(TypeUtilisateur.BIBLIOTHECAIRE)
                    .cautionDisponible(new BigDecimal("0.00"))
                    .actif(true)
                    .build());

            Utilisateur alice = utilisateurRepository.save(Utilisateur.builder()
                    .nom("Durand")
                    .prenom("Alice")
                    .email("alice@etu.fr")
                    .motDePasse(passwordEncoder.encode("alice123"))
                    .role(Role.USER)
                    .type(TypeUtilisateur.ETUDIANT)
                    .cautionDisponible(new BigDecimal("120.00"))
                    .actif(true)
                    .build());

            Utilisateur mehdi = utilisateurRepository.save(Utilisateur.builder()
                    .nom("Benali")
                    .prenom("Mehdi")
                    .email("mehdi@etu.fr")
                    .motDePasse(passwordEncoder.encode("mehdi123"))
                    .role(Role.USER)
                    .type(TypeUtilisateur.ETUDIANT)
                    .cautionDisponible(new BigDecimal("60.00"))
                    .actif(true)
                    .build());

            Utilisateur clara = utilisateurRepository.save(Utilisateur.builder()
                    .nom("Martin")
                    .prenom("Clara")
                    .email("clara@prof.fr")
                    .motDePasse(passwordEncoder.encode("clara123"))
                    .role(Role.USER)
                    .type(TypeUtilisateur.ENSEIGNANT)
                    .cautionDisponible(new BigDecimal("200.00"))
                    .actif(true)
                    .build());

            // =========================
            // EMPLACEMENTS
            // =========================
            Emplacement info = emplacementRepository.save(Emplacement.builder()
                    .code("INFO-A1")
                    .libelle("Informatique")
                    .build());

            Emplacement java = emplacementRepository.save(Emplacement.builder()
                    .code("JAVA-B2")
                    .libelle("Java")
                    .build());

            Emplacement revues = emplacementRepository.save(Emplacement.builder()
                    .code("REV-R1")
                    .libelle("Revues")
                    .build());

            // =========================
            // RESSOURCES
            // =========================
            Livre cleanCode = (Livre) ressourceRepository.save(Livre.builder()
                    .titre("Clean Code")
                    .auteur("Robert C. Martin")
                    .anneePublication(2008)
                    .theme("Programmation")
                    .cautionExigee(new BigDecimal("25.00"))
                    .emplacement(info)
                    .isbn("9780132350884")
                    .build());

            Livre effectiveJava = (Livre) ressourceRepository.save(Livre.builder()
                    .titre("Effective Java")
                    .auteur("Joshua Bloch")
                    .anneePublication(2018)
                    .theme("Java")
                    .cautionExigee(new BigDecimal("30.00"))
                    .emplacement(java)
                    .isbn("9780134685991")
                    .build());

            Livre spring = (Livre) ressourceRepository.save(Livre.builder()
                    .titre("Spring in Action")
                    .auteur("Craig Walls")
                    .anneePublication(2022)
                    .theme("Spring")
                    .cautionExigee(new BigDecimal("35.00"))
                    .emplacement(java)
                    .isbn("9781617298691")
                    .build());

            Revue javaMag = (Revue) ressourceRepository.save(Revue.builder()
                    .titre("Java Magazine")
                    .auteur("Oracle")
                    .anneePublication(2024)
                    .theme("Java")
                    .cautionExigee(new BigDecimal("10.00"))
                    .emplacement(revues)
                    .numero(128)
                    .build());

            // =========================
            // EXEMPLAIRES
            // =========================
            Exemplaire ex1 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-001")
                    .statut(StatutExemplaire.EMPRUNTE)
                    .ressource(cleanCode)
                    .build());

            Exemplaire ex2 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-002")
                    .statut(StatutExemplaire.DISPONIBLE)
                    .ressource(cleanCode)
                    .build());

            Exemplaire ex3 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-003")
                    .statut(StatutExemplaire.EMPRUNTE)
                    .ressource(effectiveJava)
                    .build());

            Exemplaire ex4 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-004")
                    .statut(StatutExemplaire.DISPONIBLE)
                    .ressource(spring)
                    .build());

            Exemplaire ex5 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-005")
                    .statut(StatutExemplaire.EMPRUNTE)
                    .ressource(javaMag)
                    .build());

            Exemplaire ex6 = exemplaireRepository.save(Exemplaire.builder()
                    .codeBarres("EX-006")
                    .statut(StatutExemplaire.DISPONIBLE)
                    .ressource(spring)
                    .build());

            // =========================
            // EMPRUNTS
            // =========================
            empruntRepository.save(Emprunt.builder()
                    .utilisateur(alice)
                    .exemplaire(ex1)
                    .dateEmprunt(LocalDate.now().minusDays(3))
                    .dateRetourPrevue(LocalDate.now().plusDays(10))
                    .statut(StatutEmprunt.EN_COURS)
                    .build());

            empruntRepository.save(Emprunt.builder()
                    .utilisateur(mehdi)
                    .exemplaire(ex3)
                    .dateEmprunt(LocalDate.now().minusDays(20))
                    .dateRetourPrevue(LocalDate.now().minusDays(5))
                    .statut(StatutEmprunt.EN_RETARD)
                    .build());

            empruntRepository.save(Emprunt.builder()
                    .utilisateur(alice)
                    .exemplaire(ex5)
                    .dateEmprunt(LocalDate.now().minusDays(15))
                    .dateRetourPrevue(LocalDate.now().minusDays(2))
                    .statut(StatutEmprunt.EN_RETARD)
                    .build());

            empruntRepository.save(Emprunt.builder()
                    .utilisateur(clara)
                    .exemplaire(ex6)
                    .dateEmprunt(LocalDate.now().minusDays(10))
                    .dateRetourPrevue(LocalDate.now().minusDays(5))
                    .dateRetour(LocalDate.now().minusDays(3))
                    .statut(StatutEmprunt.RETOURNE)
                    .build());
        };
    }
}