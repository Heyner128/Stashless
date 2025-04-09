FROM gradle:8.12.0-jdk21 AS build
COPY src /app/src
COPY build.gradle.kts /app
COPY settings.gradle.kts /app
WORKDIR /app
RUN gradle bootJar -x test

FROM openjdk:21-jdk-slim
COPY --from=build /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
