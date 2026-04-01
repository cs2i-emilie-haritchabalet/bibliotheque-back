# Bibliothèque universitaire - Backend Spring Boot

Backend REST généré à partir du rapport de conception UML.

## Ce qui est couvert

- authentification via Spring Security + HTTP Basic
- création d'utilisateurs
- recherche simple et avancée de ressources
- détail d'une ressource et disponibilité des exemplaires
- emprunt d'un exemplaire
- retour d'un exemplaire
- liste des emprunts d'un utilisateur
- liste des retards
- envoi d'une relance mail

## Ajustements apportés par rapport au rapport

1. Le rapport mentionne Angular + Spring Boot. Ici, le livrable fourni est le **backend Spring Boot** uniquement.
2. L'authentification est implémentée avec **HTTP Basic + endpoint `/api/auth/login`** au lieu d'un mécanisme JWT.
3. Le modèle métier reste conforme au rapport : utilisateurs, ressources, exemplaires, emprunts, emplacements, relances.
4. Le pattern Factory est appliqué à la création des ressources documentaires.
5. L'automate d'états des emprunts est simplifié avec les statuts `EN_COURS`, `EN_RETARD`, `RETOURNE`.

## Comptes de démonstration

- Bibliothécaire : `admin@biblio.fr` / `admin123`
- Utilisateur : `alice@etu.fr` / `alice123`

## Lancer le projet

```bash
mvn spring-boot:run
```

## H2 Console

- URL : `http://localhost:8080/h2-console`
- JDBC URL : `jdbc:h2:mem:bibliotheque`
- User : `sa`
- Password : vide

## Exemples d'appels

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@biblio.fr","motDePasse":"admin123"}'
```

### Ajouter une ressource

```bash
curl -X POST http://localhost:8080/api/ressources \
  -u admin@biblio.fr:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "type":"LIVRE",
    "titre":"Clean Code",
    "auteur":"Robert C. Martin",
    "anneePublication":2008,
    "theme":"Développement",
    "cautionExigee":20,
    "emplacementCode":"A1",
    "emplacementLibelle":"Informatique",
    "isbn":"9780132350884",
    "nombreExemplaires":2
  }'
```

### Rechercher une ressource

```bash
curl -u alice@etu.fr:alice123 "http://localhost:8080/api/ressources/search?titre=clean"
```

### Emprunter

```bash
curl -X POST http://localhost:8080/api/emprunts \
  -u alice@etu.fr:alice123 \
  -H "Content-Type: application/json" \
  -d '{"utilisateurId":2,"ressourceId":1}'
```

## Rapport : sections à mettre à jour

- **5.2** : préciser que l'authentification retenue dans l'implémentation est Spring Security en HTTP Basic.
- **6.6** : ajouter les classes `SecurityConfig`, `AuthService`, `ApiExceptionHandler`.
- **7** : indiquer que l'IHM Angular reste prévue mais non fournie dans ce livrable.
- **9.1** : ajouter des tests Spring Security et des tests de validation API.
