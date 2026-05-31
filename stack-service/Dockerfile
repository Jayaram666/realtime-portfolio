FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY target/*.jar app.jar

RUN chown spring:spring app.jar

USER spring

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]