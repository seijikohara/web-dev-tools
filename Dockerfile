# Stage 1: Build
FROM eclipse-temurin:25.0.3_9-jdk@sha256:12e44624adee6808a36d962717e1656e0afeeeff5a100f9cb00e0136513558f0 AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# Stage 2: Extract JAR
FROM eclipse-temurin:25.0.3_9-jdk@sha256:12e44624adee6808a36d962717e1656e0afeeeff5a100f9cb00e0136513558f0 AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# Stage 3: Generate AOT Cache
# Use eclipse-temurin:25-jre (same Temurin version as distroless runtime)
FROM eclipse-temurin:25.0.3_9-jre@sha256:f19dbf0a22d0b3658fda48ce7d7181df05ad14bda151dd5ad12cc09d1451c70e AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# -Xlog:aot=off: Suppress expected warnings for dynamic proxies and CGLIB classes
RUN java -Xlog:aot=off -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

# Stage 4: Runtime (uses same Temurin-25.0.1+8 as aot-builder for AOT cache compatibility)
FROM gcr.io/distroless/java25-debian13@sha256:73f2263db8defa233004a7c700fd81e25c8747a530c413bddf74367b68663468
WORKDIR /app
COPY --from=aot-builder /app ./
EXPOSE 20000
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
