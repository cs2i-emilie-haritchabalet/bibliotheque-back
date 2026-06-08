    # Build stage
    FROM maven:eclipse-temurin:17-jre-alpine AS build

    WORKDIR /app
    COPY . .

    RUN mvn clean package -DskipTests

    # Runtime stage
    FROM eclipse-temurin:17-jre

    WORKDIR /app

    COPY --from=build /app/target/*.jar app.jar

    # création user non-root
    RUN useradd -m appuser

    USER appuser

    EXPOSE 8080

    ENTRYPOINT ["java","-jar","app.jar"]
