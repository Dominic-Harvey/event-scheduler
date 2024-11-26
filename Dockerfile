FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Dominic Harvey dominiceharvey@gmail.com"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/event-scheduler.jar
ADD ${JAR_FILE} app.jar
# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]