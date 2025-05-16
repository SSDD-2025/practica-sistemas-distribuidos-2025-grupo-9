#################################################
# Imagen base para el contenedor de compilación
#################################################
FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder

# Define el directorio de trabajo donde ejecutar comandos
WORKDIR /project

# Copia el codigo del proyecto
COPY /src /project/src
COPY pom.xml /project/

RUN mvn install

RUN mvn -B clean verify

# Compila y descarga librerias
RUN mvn -B package -DskipTests

#################################################
# Imagen base para el contenedor de la aplicación
#################################################
FROM eclipse-temurin:21-jre
WORKDIR /usr/src/app/
COPY --from=builder /project/target/*.jar /usr/src/app/app.jar
EXPOSE 8443
CMD [ "java", "-jar", "app.jar" ]