# Stage 1: Build
FROM eclipse-temurin:25.0.4_7-jdk@sha256:97014c4b396021f9ddb7d592a7dbedb0c4e4215c29e03dc01c393558aefb71c2 AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# Stage 2: Extract JAR
FROM eclipse-temurin:25.0.4_7-jdk@sha256:97014c4b396021f9ddb7d592a7dbedb0c4e4215c29e03dc01c393558aefb71c2 AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# Stage 3: Generate AOT Cache
# Use eclipse-temurin:25-jre (same Temurin version as distroless runtime)
FROM eclipse-temurin:25.0.4_7-jre@sha256:bb036ed6cfdc57e3da7c22634d15f1b840d2caf76183861c80e81ca4b5104abb AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# -Xlog:aot=off: Suppress expected warnings for dynamic proxies and CGLIB classes
RUN java -Xlog:aot=off -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

# Stage 4: Runtime (uses same Temurin-25.0.1+8 as aot-builder for AOT cache compatibility)
FROM gcr.io/distroless/java25-debian13@sha256:6b3cc781107c28f82934250e56b088c44db3502cb5e9e0335d669fe8210df1cc
WORKDIR /app
COPY --from=aot-builder /app ./
EXPOSE 20000
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
