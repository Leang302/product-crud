# ---------- Build stage ----------
FROM gradle:8.6-jdk17-alpine AS builder
WORKDIR /app

# Copy only build files first (better cache)
COPY gradle gradle
COPY gradlew .
COPY build.gradle* settings.gradle* ./

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build jar
RUN ./gradlew bootJar --no-daemon


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-root user (security best practice)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]