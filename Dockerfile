FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src

# Gradle 캐시 최적화
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=0 /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
