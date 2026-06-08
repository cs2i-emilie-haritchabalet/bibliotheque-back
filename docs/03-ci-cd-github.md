# 3. CI/CD GitHub Actions

---

## Objectif

GitHub est utilisé comme plateforme principale DevSecOps.

---

## Pipelines

### 1. Quality
- SonarQube (analyse qualité)
- Semgrep (SAST)

### 2. Security
- Dependency Check (SCA)
- Trivy (scan image Docker)

### 3. Secrets
- Gitleaks

### 4. SBOM
- génération inventaire logiciel

### 5. DAST
- OWASP ZAP

### 6. Documentation
- génération automatique docs

---

## Principe DevSecOps

Sécurité intégrée dès le développement (Shift Left).

---

## Résultat

Chaque commit déclenche une analyse complète du code et des dépendances.
