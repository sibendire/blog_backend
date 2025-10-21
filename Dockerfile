# # Step 1: Use an official JDK image to build the app
# FROM eclipse-temurin:17-jdk-alpine AS builder

# WORKDIR /app

# # Copy project files
# COPY . .

# # Give permission to mvnw
# RUN chmod +x mvnw

# # Build the app (skipping tests for speed)
# RUN ./mvnw clean package -DskipTests

# # Step 2: Run the Spring Boot app from the JAR
# FROM eclipse-temurin:17-jre-alpine

# WORKDIR /app

# # Copy only the built JAR from builder stage
# COPY --from=builder /app/target/*.jar app.jar

# # Expose port 8080
# EXPOSE 8080

# # Run the app
# ENTRYPOINT ["java", "-jar", "app.jar"]
