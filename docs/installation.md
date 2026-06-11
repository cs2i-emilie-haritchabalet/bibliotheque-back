# 7. Installation

---

## Prérequis

- Java 17
- Maven 3.9+
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
L'image utilise eclipse-temurin:17-jre-alpine (image légère, sans Pebble).
---

## Variables d’environnement
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

---

## CI/CD
- GitHub Actions activé automatiquement
- GitLab CI via runner Docker

---
## Versions clés des dépendances
| Composant         | Version |
|-------------------|----------|
| Spring Boot       | 3.5.14 |
| Tomcat Embed      | 10.1.55 |
| Spring Security   | 6.5.10 |
| Spring Framework  | 6.2.18 |
| commons-lang      | 33.18.0 |
| angus-activation  | 2.0.4 |
| springdoc-openapi | 2.8.17 |
| PostgreSQL driver | 42.7.11 |
