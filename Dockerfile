FROM openjdk:17 AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package

FROM openjdk:17
WORKDIR /v3
COPY --from=build target/*.jar v3.jar
ENTRYPOINT ["java", "-jar", "v3.jar"]