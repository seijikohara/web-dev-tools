FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY ./ ./
RUN ./gradlew clean npmRunBuild build -x test --no-daemon --stacktrace

FROM gcr.io/distroless/java25-debian13
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar ./app.jar
EXPOSE 20000
ENTRYPOINT ["java", "-jar", "./app.jar"]
