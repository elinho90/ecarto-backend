# Documentation Complète : Plateforme E-Carto

## 1. Introduction
**E-Carto** est une solution web centralisée conçue pour la gestion et la cartographie des projets informatiques. Développée dans le cadre d'un stage à la **GS2E (Eranove Academy)**, cette plateforme permet de suivre le cycle de vie des projets, de visualiser leur implantation géographique et de gérer les rapports techniques associés.

## 2. Architecture Technique
La plateforme repose sur une architecture moderne et découplée (Frontend/Backend) assurant scalabilité et maintenance facilitée.

### Backend (Cœur du Système)
- **Langage** : Java 17
- **Framework** : Spring Boot 3.2
- **Sécurité** : Spring Security avec Authentification Stateless JWT (JSON Web Token)
- **Base de Données** : PostgreSQL
- **Migration de Données** : Flyway
- **Persistance** : Spring Data JPA / Hibernate
- **Génération de Rapports** : OpenPDF (LibrePDF)
- **Documentation API** : Swagger / OpenAPI 3

### Frontend (Interface Utilisateur)
- **Framework** : Angular 17+ (Architecture Standalone)
- **Design System** : Angular Material (Thème personnalisé Orange/Vibrant)
- **Cartographie** : Leaflet.js (OpenStreetMap)
- **Style** : SCSS & TailwindCSS
- **Communication** : RxJS & HttpClient

## 3. Fonctionnalités Clés

### 📊 Tableau de Bord (Dashboard)
Visualisation instantanée des métriques clés (projets actifs, sites cartographiés, notifications récentes).

### 📁 Gestion des Projets
- **Cycle de vie** : Création, modification et suivi de la progression.
- **Interactivité** : Mise à jour dynamique du pourcentage d'avancement (Progression).
- **Classification** : Organisation par type de projet et niveau de priorité.

### 🗺️ Cartographie (SIG)
Visualisation géographique des sites de projets sur une carte interactive, permettant une vision d'ensemble de l'emprise territoriale des activités.

### 📄 Gestion des Rapports
- **Stockage Centralisé** : Upload et archivage des rapports de faisabilité.
- **Exportation** : Génération de fiches projets au format PDF.
- **Partage** : Envoi direct des rapports par email depuis la plateforme.

### 👤 Administration & Sécurité
- **RBAC (Role-Based Access Control)** : Gestion des accès par rôles (Administrateur, Chef de Projet, Analyste, Décideur).
- **Audit** : Journal d'activité (Notifications) pour tracer chaque modification importante (Projets, Sites, Utilisateurs).

## 4. Design & Expérience Utilisateur (UX)
- **Identité Visuelle** : Thème premium moderne basé sur une palette orange/sombre.
- **Composants Dynamiques** : Logo interactif (Network SVG) et filigrane de marque pour renforcer l'image de l'entreprise.
- **Réactivité** : Interface adaptative fonctionnant sur PC et tablettes.

## 5. Guide d'Installation

### Prérequis
- JDK 17
- Node.js 18+ & Angular CLI
- PostgreSQL 15+

### Backend
1. Configurer la base de données dans `application.properties`.
2. Lancer le serveur : `./mvnw spring-boot:run`

### Frontend
1. Installer les dépendances : `npm install`
2. Lancer l'application : `npm start` (disponible sur http://localhost:4200)

---
*Ce document sert de support à la présentation de fin de stage.*
