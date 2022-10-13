FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY ./ ./
RUN ["./gradlew", "--no-daemon", "--scan", "build", "-x", "test"]
EXPOSE 20000
ENTRYPOINT ["java", "-jar", "./build/libs/web-dev-tools-1.0.0.jar"]
