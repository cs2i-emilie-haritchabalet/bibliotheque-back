# 4. CI/CD GitLab

---

## Objectif

GitLab est utilisé comme moteur CI/CD avec un runner Docker.

---

## Pipeline

### Étapes :

- 1) build — Maven (mvn clean package)
- 2) test — JUnit + JaCoCo (couverture de code)
- 3) quality — Checkstyle (google_checks), SpotBugs + Find Security Bugs
- 4) sca — OWASP Dependency Check (seuil CVSS ≥ 7.0, fichier suppression.xml)
- 5) build image — Docker multi-stage (eclipse-temurin:17-jre-alpine)
- 6) scan image — Trivy (vulnérabilités OS + JAR + binaires Go)
- 7) DAST — OWASP ZAP (manuel)

---

## GitLab Runner

Runner Docker utilisé pour exécuter les jobs CI.

---

## Rôle

GitLab sert de pipeline d’intégration continue simplifié et reproductible.
