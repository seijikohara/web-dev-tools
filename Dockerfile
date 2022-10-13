FROM node:18-alpine AS frontendBuilder
WORKDIR /app
COPY ./ ./
WORKDIR /app/frontend
RUN npm install
RUN npm run build -- --dest /app/src/main/resources/static

FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=frontendBuilder /app ./
RUN ./gradlew build -x test --no-daemon --stacktrace
EXPOSE 20000
ENTRYPOINT ["java", "-jar", "./build/libs/web-dev-tools-1.0.0.jar"]
