FROM openjdk:17-jdk-slim

LABEL maintainer="E-Carto Team"
LABEL version="1.0"
LABEL description="E-Carto Backend Application"

# Installation des dépendances
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Création du répertoire de l'application
WORKDIR /app

# Création du répertoire pour les uploads
RUN mkdir -p /app/uploads /app/logs

# Copie du fichier JAR
COPY target/stage-eranove-academy-0.0.1-SNAPSHOT.jar app.jar

# Création d'un utilisateur non-root
RUN groupadd -r ecarto && useradd -r -g ecarto ecarto
RUN chown -R ecarto:ecarto /app

# Changement d'utilisateur
USER ecarto

# Exposition du port
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]