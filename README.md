# E-Carto Backend

Backend Spring Boot pour la plateforme E-Carto de gestion des projets informatiques.

## 🚀 Fonctionnalités

- **Gestion des projets** : CRUD complet avec recherche multicritères
- **Gestion des rapports** : Upload et analyse de rapports de faisabilité (PDF/Word)
- **Authentification JWT** : Sécurité avec tokens JWT
- **API REST** : Endpoints documentés avec OpenAPI/Swagger
- **Base de données** : PostgreSQL avec migrations Flyway
- **Monitoring** : Logs et métriques intégrés

## 📋 Prérequis

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Docker & Docker Compose (optionnel)

## 🛠️ Installation

### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd stage-eranove-academy
```

### 2. Configuration de la base de données

#### Option A : Base de données locale
```bash
# Créer la base de données
createdb e_carto_db

# Créer l'utilisateur
createuser e_carto_user --pwprompt
```

#### Option B : Docker Compose
```bash
# Démarrer PostgreSQL et pgAdmin
docker-compose up -d postgres pgadmin

# Accéder à pgAdmin : http://localhost:5050
# Email : admin@ecarto.com
# Mot de passe : admin123
```

### 3. Configuration de l'application

Modifier `src/main/resources/application.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/e_carto_db
spring.datasource.username=e_carto_user
spring.datasource.password=votre_mot_de_passe

# JWT (générez une clé sécurisée)
app.jwt.secret=votre_clé_secrète_très_longue_et_sécurisée
```

### 4. Compilation et lancement

```bash
# Compilation
mvn clean compile

# Lancement avec Maven
mvn spring-boot:run

# Ou avec le JAR
mvn clean package
java -jar target/stage-eranove-academy-0.0.1-SNAPSHOT.jar
```

L'application sera accessible sur : http://localhost:8080

## 📚 Documentation API

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI Docs** : http://localhost:8080/api-docs

## 🔐 Authentification

### Comptes de test
```json
{
  "admin@ecarto.com": "admin123",
  "sophie.martin@ecarto.com": "password123",
  "jean.dupont@ecarto.com": "password123"
}
```

### Flow d'authentification
1. **Login** : `POST /api/auth/login`
2. **Utiliser le token** : Header `Authorization: Bearer <token>`
3. **Refresh token** : `POST /api/auth/refresh-token`

## 📁 Structure du projet

```
src/main/java/com/gs2e/stage_eranove_academy/
├── common/              # Classes communes et utilitaires
├── projet/              # Module de gestion des projets
│   ├── controller/      # Controllers REST
│   ├── dto/            # Data Transfer Objects
│   ├── mapper/         # MapStruct mappers
│   ├── model/          # Entités JPA
│   ├── repository/     # Repositories Spring Data
│   ├── service/        # Services métier
│   └── validator/      # Validateurs
├── rapport/             # Module de gestion des rapports
├── security/            # Module de sécurité et authentification
└── typeprojet/          # Module de gestion des types de projet
```

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests avec couverture
mvn clean test jacoco:report

# Tests d'intégration
mvn verify
```

## 🐳 Docker

### Construction de l'image
```bash
docker build -t e-carto-backend .
```

### Lancement avec Docker Compose
```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f app

# Arrêter les services
docker-compose down
```

## 📊 Monitoring

### Logs
```bash
# Logs applicatifs
tail -f logs/application.log

# Logs Spring Boot
tail -f logs/spring-boot-logger.log
```

### Métriques
- **Health Check** : `GET /actuator/health`
- **Métriques** : `GET /actuator/metrics` (avec Spring Boot Actuator)

## 🔧 Configuration

### Variables d'environnement importantes
```bash
# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/e_carto_db
SPRING_DATASOURCE_USERNAME=e_carto_user
SPRING_DATASOURCE_PASSWORD=e_carto_password

# JWT
APP_JWT_SECRET=votre_clé_secrète

# Upload
APP_UPLOAD_DIR=/path/to/uploads
```

### Profils Spring
- `dev` : Développement (H2, logs détaillés)
- `prod` : Production (PostgreSQL, logs optimisés)
- `docker` : Environnement Docker

## 🚨 Troubleshooting

### Problèmes courants

1. **Port déjà utilisé**
```bash
# Vérifier les ports en cours d'utilisation
netstat -an | grep 8080

# Changer le port dans application.properties
server.port=8081
```

2. **Erreur de connexion PostgreSQL**
```bash
# Vérifier que PostgreSQL est en cours d'exécution
sudo systemctl status postgresql

# Vérifier les logs PostgreSQL
tail -f /var/log/postgresql/postgresql-*.log
```

3. **Erreur de compilation MapStruct**
```bash
# Nettoyer et recompiler
mvn clean
mvn compile
```

## 🤝 Contribution

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Équipe

- **Kayre Elie** - Lead Developer
- **Kayre Elie** - Backend Developer
- **Kayre Elie**** - DevOps Engineer

## 📞 Support

Pour toute question ou problème :
- Créer une issue sur GitHub
- Contacter l'équipe : support@ecarto.com
- Documentation :ecarto-backend
