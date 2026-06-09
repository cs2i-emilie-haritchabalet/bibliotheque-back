# 5. DevSecOps Pipeline

---

## Définition

DevSecOps = DevOps + sécurité intégrée dès le début du cycle de développement.

---

## Principe SHIFT LEFT

La sécurité est intégrée :
- au développement
- aux commits
- aux pipelines CI
- aux tests runtime

---

## Chaîne complète

### 1. Code
- Pre-commit hooks (détection locale avant commit)
- Gitleaks (détection de secrets)
- Alias `git sync` : push simultané vers GitHub et GitLab en une seule commande

### 2. Build
- compilation Maven
- tests unitaires (JUnit)
- couverture de code (JaCoCo)

### 3. Linting / Qualité
- Checkstyle (style Java, google_checks)
- SpotBugs + Find Security Bugs (analyse statique bytecode, détection failles)

### 4. SAST
- Semgrep
- SonarCloud

### 5. SCA
- OWASP Dependency Check (seuil CVSS ≥ 7.0)
- fichier suppression.xml pour faux positifs documentés

### 6. Container security
- Trivy (OS, JAR, binaires Go)
- image Alpine (suppression de Pebble et ses CVE Go)
- user non-root dans le conteneur

### 7. Supply chain
- SBOM (inventaire composants)

### 8. DAST
- OWASP ZAP

---

## Objectif

Réduire les vulnérabilités avant production.

---

## Résultat

Pipeline automatisé couvrant :
- code
- dépendances
- infrastructure
- exécution
