# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# Stage 2: Extract JAR
FROM eclipse-temurin:25-jdk AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# Stage 3: Generate AOT Cache
# Use eclipse-temurin:25-jre (same Temurin version as distroless runtime)
FROM eclipse-temurin:25-jre AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# -Xlog:aot=off: Suppress expected warnings for dynamic proxies and CGLIB classes
RUN java -Xlog:aot=off -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

# Stage 4: Runtime (uses same Temurin-25.0.1+8 as aot-builder for AOT cache compatibility)
FROM gcr.io/distroless/java25-debian13
WORKDIR /app
COPY --from=aot-builder /app ./
EXPOSE 20000
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
