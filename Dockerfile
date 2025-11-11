FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x spotlessCheck -x spotlessKotlinCheck --stacktrace

FROM gcr.io/distroless/java25-debian13
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar ./app.jar
EXPOSE 20000
ENTRYPOINT ["java", "-jar", "./app.jar"]
