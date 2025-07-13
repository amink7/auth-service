# Usa una imagen base con Java
FROM openjdk:17-jdk-alpine

# Argumento para nombre de jar (opcional, por si quieres parametrizar)
ARG JAR_FILE=target/auth-service-0.0.1-SNAPSHOT.jar

# Copia el jar al contenedor
COPY ${JAR_FILE} app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java","-jar","/app.jar"]
