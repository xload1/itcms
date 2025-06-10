# -------- Stage 1: build with Gradle --------
FROM gradle:8-jdk17-alpine AS builder

WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle/

COPY src src/

RUN ./gradlew clean build -x test

# -------- Stage 2: runtime --------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]