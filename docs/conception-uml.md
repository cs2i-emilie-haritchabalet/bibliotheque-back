# 1. Conception UML

## Objectif

La phase de conception a permis de modéliser le système de bibliothèque universitaire avant implémentation.

---

## Diagrammes réalisés

- Diagramme de classes :
    - Utilisateur
    - Ressource
    - Exemplaire
    - Emprunt

- Diagramme de cas d’utilisation :
    - Emprunter un document
    - Retourner un document
    - Rechercher une ressource
    - Gérer les utilisateurs

---

## Architecture métier initiale

Le modèle suit une logique classique de gestion documentaire :
- un utilisateur possède plusieurs emprunts
- une ressource possède plusieurs exemplaires
- un exemplaire peut être emprunté ou disponible

---

## Évolution

Cette version UML correspond à la première phase du projet (évaluation initiale), avant intégration DevSecOps.
