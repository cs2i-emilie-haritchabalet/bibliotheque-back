# 6. Outils de sécurité

---

## SonarCloud
Analyse qualité, bugs et code smells. Intégré via propriétés Maven dans pom.xml.

---

## Semgrep
Analyse statique (SAST) — détection de patterns de vulnérabilités dans le code source.

---

## Checkstyle
Linter Java configuré avec google_checks.xml. Exécuté à la phase verify.

---

## SpotBugs + Find Security Bugs
Analyse statique du bytecode Java. Find Security Bugs ajoute 138 détecteurs de vulnérabilités
(injections SQL, XSS, mauvaise configuration Spring Security, cryptographie faible…).
Exécuté à la phase verify, fait échouer le build en cas de bug détecté.

---

## OWASP Dependency Check
Analyse des vulnérabilités des dépendances Java (SCA).
- seuil configuré : CVSS ≥ 7.0
- faux positifs gérés via suppression.xml
- rapport HTML/JSON généré à chaque build

---

## Trivy
Scan des images Docker : vulnérabilités OS, dépendances JAR, binaires Go.
Utilisé en CI GitLab après build de l'image.

---

## Gitleaks
Détection de secrets (API keys, tokens, mots de passe) dans l'historique Git.

---

## OWASP ZAP
Test dynamique de sécurité (DAST) — scan de l'application en cours d'exécution.

---

## SBOM (Syft / Anchore)
Inventaire des composants logiciels (Software Bill of Materials).
