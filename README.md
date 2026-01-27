# E-SERVICE PLATFORM 

Ce projet implémente une plateforme complète de gestion de requêtes administratives avec gestion des rôles et sécurité par jetons.

[![Tech Stack](https://img.shields.io/badge/Backend-Java%20%7C%20Spring%20Security%20%7C%20JWT-blue)]()
[![Frontend](https://img.shields.io/badge/Frontend-React%20%7C%20Tailwind%20CSS-blue)]()
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20(Docker)-blue)]()

---

##  *FR* Version Française (English below)

### Fonctionnalités Clés

1.  **Authentification JWT (Sécurité) :** Système sécurisé utilisant **Spring Security** et **JSON Web Tokens (JWT)**.
2.  **Gestion des Rôles :** Distingue l'accès entre `ROLE_USER` (création/soumission de requêtes) et `ROLE_ADMIN` (approbation/rejet) via l'annotation `@PreAuthorize`.
3.  **Cycle de Vie (BPM) :** Gère le flux de la requête : `DRAFT` -> `SUBMITTED` -> `APPROVED` / `REJECTED`.

### Backend (Java / Spring Boot)

* Java 17+, Spring Boot 3, Spring Security, Spring Data JPA, JJWT.
* PostgreSQL (persistant via Docker Compose).

### Frontend (React / Vite)

* React.js (Vite), Tailwind CSS.

##  Lancement de l'application

### 1. Prérequis

* Docker Desktop (en cours d'exécution).
* Node.js & npm (pour le Frontend).
* JDK 17+ & Maven (pour le Backend).

### 2. Démarrage de l'Infrastructure (Backend et Base de Données)

Exécutez ces commandes depuis le répertoire **racine du Backend** (`e-service-platform/`) :

```bash
# Lancer la base de données PostgreSQL en conteneur Docker
docker compose up -d
```

```bash
# Lancer l'application Spring Boot dans votre IDE
(Ouvrez le projet et exécutez la classe EServicePlatformApplication)
```

### 3. Démarrage du Frontend (React)

Ouvrez un nouveau terminal dans le dossier frontend-react-app/ :

```bash
# Installer les dépendances (première fois uniquement)
npm install lucide-react
```

```bash
# Lancer le serveur de développement React
npm run dev
```

L'application Frontend sera disponible à l'adresse indiquée par Vite (généralement http://localhost:5173).

### 4. Identifiants de Test

Utilisez ces identifiants pour tester le flux sécurisé (créés par le DataInitializer au premier lancement du Backend) :

| Rôle | Username | Password | Actions Autorisées |
| :--- | :--- | :--- | :--- |
| **Administrateur** | admin | adminpass | LOGIN, VIEW ADMIN DASHBOARD, APPROVE, REJECT |
| **Utilisateur** | user1 | userpass | LOGIN, SUBMIT FORM |



---
##  *ENG* English Version

### Key Features

1.  **JWT Authentication :** Secure system using **Spring Security** and **JSON Web Tokens (JWT)**.
2.  **Role Management :** Distinguishes access between `ROLE_USER` (create/submit requests) and `ROLE_ADMIN` (approval/rejection) via the `@PreAuthorize` annotation.
3.  **Cycle de Vie (BPM) :** Gère le flux de la requête : `DRAFT` -> `SUBMITTED` -> `APPROVED` / `REJECTED`.

### Backend (Java / Spring Boot)

* Java 17+, Spring Boot 3, Spring Security, Spring Data JPA, JJWT.
* PostgreSQL (persistent via Docker Compose).

### Frontend (React / Vite)

* React.js (Vite), Tailwind CSS.

##  Application launch

### 1. Prerequisites

* Docker Desktop (in progress).
* Node.js & npm (Frontend).
* JDK 17+ & Maven (Backend).

### 2. Infrastructure Launch (Backend and Database)

Run these commands from the **Backend root** directory (`e-service-platform/`) :

```bash
# Launch PostgreSQL via Docker
docker compose up -d
```

```bash
# Launch the SpringBoot application frome the IDE
# (Open the project and run the EServicePlatformApplication class)
```

### 3.  Frontend Launch (React)

Open a new terminal in the frontend-react-app/ repository :

```bash
# Install dependencies (first time only)
npm install lucide-react
``` 

```bash
# Launch React server
npm run dev
```

The Frontend application will be available at the address provided by Vite. (generally http://localhost:5173).

### 4. Test Logins

Use these credentials to test the secure flow (created by DataInitializer when the Backend is launched for the first time):

| Rôle | Username | Password | Actions Autorisées |
| :--- | :--- | :--- | :--- |
| **Administrateur** | admin | adminpass | LOGIN, VIEW ADMIN DASHBOARD, APPROVE, REJECT |
| **Utilisateur** | user1 | userpass | LOGIN, SUBMIT FORM |

