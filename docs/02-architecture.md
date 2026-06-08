# 2. Architecture du système

---

## Stack technique

- Backend : Spring Boot (Java 17)
- Frontend : Angular (non inclus ici)
- Base de données : PostgreSQL / H2 (dev)
- Containerisation : Docker
- CI/CD : GitHub Actions + GitLab CI

---

## Architecture globale
Frontend Angular → API Spring Boot → PostgreSQL

---

## Sécurité intégrée

- Spring Security (HTTP Basic)
- contrôle d’accès par rôles
- validation des entrées

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
