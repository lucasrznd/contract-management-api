FROM openjdk:17-jdk

COPY target/contract-management-api-1.0.0.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]