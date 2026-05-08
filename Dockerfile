FROM gradle:8-jdk21 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN ./gradlew :server:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /home/gradle/src/server/build/libs/*-all.jar /app/server.jar
CMD ["java", "-jar", "server.jar"]