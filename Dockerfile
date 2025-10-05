FROM eclipse-temurin:21.0.7_6-jdk-alpine-3.21 AS builder

# Argument to specify the microservice to be built
ARG SERVICE_NAME
WORKDIR /app
# Copy root Gradle resources
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .
# Copy core resources
COPY core/build.gradle core/build.gradle
COPY core/src core/src
# Copy microservice resources
COPY ${SERVICE_NAME}/build.gradle ${SERVICE_NAME}/build.gradle
COPY ${SERVICE_NAME}/src ${SERVICE_NAME}/src
# Make gradlew executable and build the microservice
RUN chmod +x gradlew && ./gradlew clean && ./gradlew :${SERVICE_NAME}:build -x test

FROM eclipse-temurin:21.0.7_6-jre-alpine-3.21

# Argument to specify the microservice to be built
ARG SERVICE_NAME
## MongoDB Connection String
#ARG DB_CONNECTION_STRING
# Include curl for health check
RUN apk --no-cache add curl
# Set environment variablefrom argument
#ENV DB_CONNECTION_STRING=$DB_CONNECTION_STRING
# Copy the built JAR from the builder stage
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
# Start the microservice
ENTRYPOINT ["java", "-jar", "/app.jar"]