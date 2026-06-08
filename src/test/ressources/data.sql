-- EMPLACEMENTS
INSERT INTO emplacements (id, code, libelle) VALUES (1, 'INFO-A1', 'Informatique');
INSERT INTO emplacements (id, code, libelle) VALUES (2, 'JAVA-B2', 'Java');
INSERT INTO emplacements (id, code, libelle) VALUES (3, 'REV-R1', 'Revues');

-- RESSOURCES (table mère)
INSERT INTO ressources (id, titre, auteur, annee_publication, theme, caution_exigee, emplacement_id)
VALUES (1, 'Clean Code', 'Robert C. Martin', 2008, 'Programmation', 25.00, 1);
INSERT INTO ressources (id, titre, auteur, annee_publication, theme, caution_exigee, emplacement_id)
VALUES (2, 'Effective Java', 'Joshua Bloch', 2018, 'Java', 30.00, 2);

-- LIVRE (table fille, JOINED)
INSERT INTO livre (id, isbn) VALUES (1, '9780132350884');
INSERT INTO livre (id, isbn) VALUES (2, '9780134685991');

-- UTILISATEURS

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, type, caution_disponible, actif)
VALUES ('Admin', 'Biblio', 'admin@biblio.fr',
        '$2a$10$Emv4GC2YYocQ4GSCJ7m2AeH.u0DHHyhum6D3DSbEEjfyTVhaW5l2K',
        'BIBLIOTHECAIRE', 'BIBLIOTHECAIRE', 0.00, true);


INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, type, caution_disponible, actif)
VALUES ('Durand', 'Alice', 'alice@etu.fr',
        '$2a$10$eXmQcieZQxLRb4VsNugU5ONvhfm3r8rpaUoRIbS60ak8W8Nn2swqu',
        'USER', 'ETUDIANT', 120.00, true);

INSERT INTO utilisateurs (id, nom, prenom, email, mot_de_passe, role, type, caution_disponible, actif)
VALUES (3, 'Benali', 'Mehdi', 'mehdi@etu.fr',
        '$2a$10$eXmQcieZQxLRb4VsNugU5ONvhfm3r8rpaUoRIbS60ak8W8Nn2swqu',
        'USER', 'ETUDIANT', 60.00, true);

-- EXEMPLAIRES
INSERT INTO exemplaires (id, code_barres, statut, ressource_id)
VALUES (1, 'EX-001', 'EMPRUNTE', 1);
INSERT INTO exemplaires (id, code_barres, statut, ressource_id)
VALUES (2, 'EX-002', 'EMPRUNTE', 2);

-- EMPRUNTS
-- id=1 : en cours (pas en retard, juste pour avoir des données)
INSERT INTO emprunts (id, utilisateur_id, exemplaire_id, date_emprunt, date_retour_prevue, statut)
VALUES (1, 2, 1, DATEADD('DAY', -3, CURRENT_DATE), DATEADD('DAY', 10, CURRENT_DATE), 'EN_COURS');

-- id=2 : EN_RETARD ← c'est celui qu'appelle envoyerRelance_devraitAutoriserLeBibliothecaire
INSERT INTO emprunts (id, utilisateur_id, exemplaire_id, date_emprunt, date_retour_prevue, statut)
VALUES (2, 3, 2, DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE), 'EN_RETARD');
