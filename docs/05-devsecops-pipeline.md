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
- Pre-commit hooks
- Gitleaks

### 2. Build
- compilation Maven
- tests unitaires

### 3. SAST
- Semgrep
- SonarQube

### 4. SCA
- OWASP Dependency Check

### 5. Container security
- Trivy

### 6. Supply chain
- SBOM (inventaire composants)

### 7. DAST
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
