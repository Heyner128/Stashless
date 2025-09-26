FROM gradle:8.13.0-jdk23 AS build
COPY src /app/src
COPY build.gradle.bck /app
COPY settings.gradle /app
WORKDIR /app
RUN gradle bootJar -x test

FROM openjdk:23-jdk
COPY --from=build /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
