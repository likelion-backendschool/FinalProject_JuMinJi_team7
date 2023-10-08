FROM openjdk:11-jdk
ARG JAR_FILE=builds/libs/1Week_Mission-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]