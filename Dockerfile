# Étape 1 : build du projet avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copier le code source et construire le jar
COPY src ./src
RUN mvn -q -Dmaven.test.skip=true package

# Étape 2 : image finale légère pour exécuter l'app
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copier le jar généré depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]