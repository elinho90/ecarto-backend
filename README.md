<h1 align="center">🗺️ E-Carto — Backend API</h1>

<p align="center">
  <strong>Plateforme de gestion cartographique de projets terrain</strong><br/>
  Développée au département <strong>DAPSI</strong> · GS2E (Groupe Eranove)
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-Secured-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
</p>

---

## 🏛️ Architecture Globale

```
┌─────────────────────────────────────────────────────────┐
│                   CLIENT (Angular 17)                    │
│          Leaflet.js · Angular Material · JWT            │
└────────────────────────┬────────────────────────────────┘
                         │ HTTPS · REST API
┌────────────────────────▼────────────────────────────────┐
│              SPRING BOOT 3.2 (API Layer)                 │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  Controllers │  │   Services   │  │  Repositories │  │
│  │  (OpenAPI)   │  │ (MapStruct)  │  │  (JPA/Hib.)   │  │
│  └──────────────┘  └──────────────┘  └───────────────┘  │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Spring Security · JWT · RBAC (Roles & Permissions)│  │
│  └──────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────┘
                         │ JDBC
┌────────────────────────▼────────────────────────────────┐
│             PostgreSQL 16 (Base de données)              │
│            Migrations gérées par Flyway                  │
└─────────────────────────────────────────────────────────┘
```

## ✨ Fonctionnalités Clés

- 📊 **Dashboard** : Métriques en temps réel sur les projets (avancement, statuts, alertes)
- 🔐 **Sécurité** : Authentification JWT + RBAC (Admin, Manager, Technicien, Viewer)
- 🗰 **Cycle de vie projets** : Création → Planification → Exécution → Clôture
- 🌍 **SIG** : Gestion des données géospatiales et points d'intervention
- 📄 **Reporting** : Génération automatique de rapports PDF et Excel (OpenPDF)
- 🔄 **Migrations** : Versionning de schéma PostgreSQL via Flyway
- 📚 **Documentation** : API complètement documentée avec Swagger / OpenAPI 3
- 🐳 **DevOps** : Conteneurisé avec Docker + docker-compose

## 🔧 Stack Technique

| Couche | Technologie |
|--------|-------------|
| Langage | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Sécurité | Spring Security · JJWT (JWT) |
| Persistance | Spring Data JPA · Hibernate |
| Base de données | PostgreSQL |
| Migrations | Flyway |
| Mapping | MapStruct · Lombok |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| PDF/Excel | OpenPDF · Apache Tika |
| Tests | JUnit 5 · Testcontainers |
| Conteneurisation | Docker · Docker Compose |

## 🚀 Lancement rapide

### Prérequis

- Java 17+
- Docker & docker-compose

### Avec Docker (recommandé)

```bash
git clone https://github.com/elinho90/ecarto-backend.git
cd ecarto-backend
docker-compose up -d
```

L'API sera disponible sur `http://localhost:8080`  
La documentation Swagger : `http://localhost:8080/swagger-ui.html`

### Sans Docker

```bash
# Configurer le fichier application.properties avec votre PostgreSQL
./mvnw spring-boot:run
```

## 🔗 Frontend

Le frontend Angular associé : [ecarto-frontend](https://github.com/elinho90/ecarto-frontend)

## 👨‍💻 Auteur

**Elie Hervé Régis Kayré** — [Portfolio](https://portfolio-kayre.vercel.app) · [LinkedIn](https://www.linkedin.com/in/elie-hervé-régis-kayre-90728b1a4)

