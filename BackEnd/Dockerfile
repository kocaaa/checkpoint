FROM openjdk:11

COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
COPY default-user.jpeg default-user.jpeg

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]