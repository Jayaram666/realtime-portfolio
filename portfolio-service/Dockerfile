FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
#RUN mvn -q -pl portfolio-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /workspace/portfolio-service/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
