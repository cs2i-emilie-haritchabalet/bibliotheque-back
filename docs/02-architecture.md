# 2. Architecture du système

---

## Stack technique

- Backend : Spring Boot (Java 17)
- Frontend : Angular (non inclus ici)
- Base de données : PostgreSQL 16 / H2 (tests)
- Containerisation : Docker (image eclipse-temurin:17-jre-alpine)
- CI/CD : GitHub Actions + GitLab CI

---

## Architecture globale
Frontend Angular → API Spring Boot → PostgreSQL

---

## Sécurité intégrée

- Spring Security 6.5.10 (HTTP Basic)
- contrôle d'accès par rôles
- validation des entrées (hibernate-validator)
- image Docker non-root (user appuser)

---

## Déploiement

- Docker Compose pour environnement local
- GitHub Actions pour analyse DevSecOps
- GitLab CI pour build et tests continus

---

## Principe clé

Architecture orientée API REST avec séparation claire des couches :
- Controller
- Service
- Repository
