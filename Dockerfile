# Stage 1: Build
FROM eclipse-temurin:25.0.5_8-jdk@sha256:83f4a7fc27df94dcc7b8de04d14cbbba8da14406f921e966969c3b09000aa507 AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# Stage 2: Extract JAR
FROM eclipse-temurin:25.0.5_8-jdk@sha256:83f4a7fc27df94dcc7b8de04d14cbbba8da14406f921e966969c3b09000aa507 AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# Stage 3: Generate AOT Cache
# Use eclipse-temurin:25-jre (same Temurin version as distroless runtime)
FROM eclipse-temurin:25.0.5_8-jre@sha256:ecdc0adb37bde1a281c4d245a3cd535004b1fc8322d560f5b09463b248c81df0 AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# -Xlog:aot=off: Suppress expected warnings for dynamic proxies and CGLIB classes
RUN java -Xlog:aot=off -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

# Stage 4: Runtime (uses same Temurin-25.0.1+8 as aot-builder for AOT cache compatibility)
FROM gcr.io/distroless/java25-debian13@sha256:d72c7b79a8c4babd6a69cf00979c9eed7f87e3e2b7d6871c6a0e9f91ff626cea
WORKDIR /app
COPY --from=aot-builder /app ./
EXPOSE 20000
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
