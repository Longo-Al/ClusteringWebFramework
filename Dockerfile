FROM openjdk:21-jdk-slim

# Aggiungere il JAR eseguibile al container
COPY build/libs/clusteringWebApp-all.jar /app/app.jar

# Comando per avviare l'applicazione
CMD ["java", "-jar", "/app/app.jar"]
