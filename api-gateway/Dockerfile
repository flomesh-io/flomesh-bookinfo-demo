FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/api-gateway.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms256M","-Xmx256M","-jar","/app.jar"]