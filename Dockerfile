FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY ./ ./
RUN ./gradlew clean npmRunBuild build -x test --no-daemon --stacktrace

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar ./app.jar
EXPOSE 20000
ENTRYPOINT ["java", "-jar", "./app.jar"]
