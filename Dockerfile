# Étape 1: Build de l'application avec Maven
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY . .
# On package l'application, en sautant les tests (déjà faits dans Jenkins)
RUN mvn clean package -DskipTests

# Étape 2: Création de l'image finale légère
FROM openjdk:17-slim
WORKDIR /app
# Copier le JAR buildé depuis l'étape précédente
COPY --from=builder /app/target/*.jar app.jar
# Exposer le port sur lequel votre application écoute (par défaut 8080 pour Spring Boot)
EXPOSE 8080
# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]