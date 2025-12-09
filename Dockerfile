# ========================================
# Stage 1: Build the application
# ========================================
# Use Java 21 for building (Kotlin toolchain requirement)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# ========================================
# Stage 2: Extract the JAR for AOT Cache
# ========================================
# Use Java 25 for runtime stages (AOT Cache support)
FROM eclipse-temurin:25-jdk AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
# Extract the JAR using Spring Boot's jarmode tools
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# ========================================
# Stage 3: Generate AOT Cache (Training Run)
# ========================================
FROM eclipse-temurin:25-jdk AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# Training run to generate AOT cache
# -Dspring.context.exit=onRefresh: Exit after ApplicationContext refresh
# -XX:AOTCacheOutput: Generate AOT cache file (Java 24+)
RUN java -XX:AOTCacheOutput=app.aot \
    -Dspring.context.exit=onRefresh \
    -jar app.jar

# ========================================
# Stage 4: Final runtime image
# ========================================
FROM gcr.io/distroless/java25-debian13
WORKDIR /app
# Copy extracted application and AOT cache
COPY --from=aot-builder /app ./
COPY --from=aot-builder /app/app.aot ./app.aot
EXPOSE 20000
# Use AOT cache for faster startup
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
