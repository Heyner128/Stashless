FROM gradle:9.1.0-jdk25 AS build
COPY src /app/src
COPY build.gradle.kts /app
COPY settings.gradle /app
WORKDIR /app
RUN gradle bootJar -x test

FROM openjdk:25-jdk
COPY --from=build /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
