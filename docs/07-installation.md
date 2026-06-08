# 7. Installation

---

## Prérequis

- Java 17
- Maven
- Docker
- Node.js (Angular front)

---

## Backend

```bash
mvn clean install
mvn spring-boot:run
```

---

## Docker
```bash
docker compose up --build
```

---

## Variables d’environnement
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

---

## CI/CD
GitHub Actions activé automatiquement
GitLab CI via runner Docker

---
