# Bibliothèque universitaire – Backend Spring Boot

Backend REST d’une application de gestion de bibliothèque universitaire avec intégration DevSecOps complète.

## Stack technique
- Java 17
- Spring Boot
- Spring Security
- PostgreSQL / H2 (dev)
- Docker
- GitHub Actions (DevSecOps)
- GitLab CI (runner infra)

## Architecture globale
Application composée de :
- API REST Spring Boot
- Base de données PostgreSQL
- Frontend Angular (non inclus dans ce livrable)
- Pipelines CI/CD GitHub + GitLab

## Fonctionnalités métier
- Authentification utilisateurs (Spring Security)
- Gestion des ressources documentaires
- Emprunts / retours
- Gestion des retards
- Relances email automatiques
- Recherche simple et avancée

## Comptes de test
- Admin : admin@biblio.fr / admin123
- User : alice@etu.fr / alice123

## Lancement du projet
### En local (Maven)
```bash
mvn spring-boot:run
```
### Avec Docker (recommandé)
```bash
docker compose up --build
```

## Accès application
- API : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui/index.html
- H2 console : http://localhost:8080/h2-console

## DevSecOps intégré
Ce projet applique une approche DevSecOps (Shift Left Security).

### Qualité & analyse code
- SonarQube → qualité + bugs
- Semgrep → SAST (analyse statique)

### Sécurité dépendances
- OWASP Dependency Check (SCA)
- détection CVE dans les dépendances Java

### Sécurité conteneur
- Trivy → scan image Docker

### Détection secrets
- Gitleaks → détection clés API / mots de passe

### Supply Chain Security
- SBOM (Software Bill of Materials)
- inventaire des composants logiciels

### Tests dynamiques
- OWASP ZAP (DAST)
- simulation d’attaques sur application en exécution

### Pre-commit (Shift Left)
- vérification locale avant commit
- blocage des secrets avant push

## CI/CD
### GitHub Actions
Pipeline DevSecOps complet :
- Quality (Sonar + Semgrep)
- Security (Dependency + Trivy)
- Secrets (Gitleaks)
- SBOM
- DAST
- Documentation

### GitLab CI
Pipeline simplifié :
- Build Maven
- Tests
- Docker build
- Scan image (Trivy)
- DAST manuel

## Documentation
Le projet inclut une documentation complète :
```json
/docs
 ├── installation.md
 ├── devsecops.md
 ├── ci-cd.md
 ├── architecture.md
 ├── runner-gitlab.md
 └── exploitation.md
```

## Choix techniques (justification)
- Spring Security HTTP Basic : simplicité + projet pédagogique
- PostgreSQL + H2 : dev/prod separation
- Docker : reproductibilité
- CI/CD multi-plateforme : approche industrielle
- DevSecOps : sécurité intégrée dès le développement

## Limites
- Frontend Angular non inclus dans ce livrable
- Authentification JWT non implémentée (choix simplifié)

## Conclusion
Ce projet démontre une chaîne complète DevSecOps :
- qualité logicielle
- sécurité applicative
- sécurité supply chain
- automatisation CI/CD
- reproductibilité
