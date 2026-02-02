FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/
COPY src /app/src

# Gradle 캐시 최적화
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Docker cli 설치
RUN apt-get update && \
    apt-get install -y docker.io && \
    rm -rf /var/lib/apt/lists/*

COPY --from=0 /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
