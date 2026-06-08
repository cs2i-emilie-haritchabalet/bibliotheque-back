# 4. CI/CD GitLab

---

## Objectif

GitLab est utilisé comme moteur CI/CD avec un runner Docker.

---

## Pipeline

### Étapes :

- build (Maven)
- test (JUnit)
- security scan
- build Docker image
- scan image (Trivy)
- DAST manuel

---

## GitLab Runner

Runner Docker utilisé pour exécuter les jobs CI.

---

## Rôle

GitLab sert de pipeline d’intégration continue simplifié et reproductible.
