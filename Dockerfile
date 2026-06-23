# Stage 1: Build
FROM eclipse-temurin:25.0.3_9-jdk@sha256:dfc0093e3dbf43dae57827111c6e374f5b44fac19a9451584b2b336b81474d64 AS builder
WORKDIR /build
COPY ./ ./
RUN ./gradlew clean --stacktrace && \
    ./gradlew npmRunBuild --stacktrace && \
    ./gradlew build -x test -x integrationTest --stacktrace

# Stage 2: Extract JAR
FROM eclipse-temurin:25.0.3_9-jdk@sha256:dfc0093e3dbf43dae57827111c6e374f5b44fac19a9451584b2b336b81474d64 AS extractor
WORKDIR /app
COPY --from=builder /build/build/libs/app.jar ./app.jar
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# Stage 3: Generate AOT Cache
# Use eclipse-temurin:25-jre (same Temurin version as distroless runtime)
FROM eclipse-temurin:25.0.3_9-jre@sha256:7ea65de6187ad8fbcc0ad155950c38664a7371148bb3ccf1ec1e1b286b44ad08 AS aot-builder
WORKDIR /app
COPY --from=extractor /app/extracted ./
# -Xlog:aot=off: Suppress expected warnings for dynamic proxies and CGLIB classes
RUN java -Xlog:aot=off -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

# Stage 4: Runtime (uses same Temurin-25.0.1+8 as aot-builder for AOT cache compatibility)
FROM gcr.io/distroless/java25-debian13@sha256:583ba2e08558063002bd1b5874a81b33b7204a0ad46727d4b6cbeff5a25935ba
WORKDIR /app
COPY --from=aot-builder /app ./
EXPOSE 20000
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
