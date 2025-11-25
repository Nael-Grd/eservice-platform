# E-SERVICE PLATFORM (Microservice Java/React Sécurisé)

Ce projet implémente une plateforme complète de gestion de requêtes administratives (e-Services), simulant les défis d'un système critique (e-Gov ou FinTech). L'architecture est Full-Stack découplée (Mono-Repo) avec gestion des rôles et sécurité par jetons.

[![Tech Stack](https://img.shields.io/badge/Backend-Java%20%7C%20Spring%20Security%20%7C%20JWT-blue)]()
[![Frontend](https://img.shields.io/badge/Frontend-React%20%7C%20Tailwind%20CSS-blue)]()
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20(Docker)-blue)]()
[![Status](https://img.shields.io/badge/Status-Complete%20%2F%20Production%20Ready-green)]()

---

##  Description du Projet

### Fonctionnalités Clés

1.  **Authentification JWT (Sécurité) :** Système sécurisé utilisant **Spring Security** et **JSON Web Tokens (JWT)**. L'architecture est sans état (`STATELESS`), essentielle pour la scalabilité Microservice/Cloud.
2.  **Gestion des Rôles :** Distingue l'accès entre `ROLE_USER` (création/soumission de requêtes) et `ROLE_ADMIN` (approbation/rejet) via l'annotation `@PreAuthorize`.
3.  **Cycle de Vie (BPM) :** Gère le flux de la requête : `DRAFT` -> `SUBMITTED` -> `APPROVED` / `REJECTED`.

### Backend (Java / Spring Boot)

* Java 17+, Spring Boot 3, Spring Security, Spring Data JPA, JJWT.
* PostgreSQL (persistant via Docker Compose).

### Frontend (React / Vite)

* React.js (Vite), Tailwind CSS (design sophistiqué).

##  Comment Lancer l'Application (Environnement de Développement)

Ce projet utilise **Docker Compose** pour l'infrastructure, assurant un démarrage simple et reproductible.

### 1. Prérequis

* Docker Desktop (en cours d'exécution).
* Node.js & npm (pour le Frontend).
* JDK 17+ & Maven (pour le Backend).

### 2. Démarrage de l'Infrastructure (Backend et Base de Données)

Exécutez ces commandes depuis le répertoire **racine du Backend** (`e-service-platform/`) :

```bash
# a. Lancer la base de données PostgreSQL en conteneur Docker
docker compose up -d

# b. Lancer l'application Spring Boot dans votre IDE
# (Ouvrez le projet et exécutez la classe EServicePlatformApplication)


3. Démarrage du Frontend (React)

Ouvrez un nouveau terminal dans le dossier frontend-react-app/ :

# a. Installer les dépendances (première fois uniquement)
npm install lucide-react

# b. Lancer le serveur de développement React
npm run dev
```

L'application Frontend sera disponible à l'adresse indiquée par Vite (généralement http://localhost:5173).

### 4. Identifiants de Test

Utilisez ces identifiants pour tester le flux sécurisé (créés par le DataInitializer au premier lancement du Backend) :

| Rôle | Username | Password | Actions Autorisées |
| :--- | :--- | :--- | :--- |
| **Administrateur** | admin | adminpass | LOGIN, VIEW ADMIN DASHBOARD, APPROVE, REJECT |
| **Utilisateur** | user1 | userpass | LOGIN, SUBMIT FORM |
