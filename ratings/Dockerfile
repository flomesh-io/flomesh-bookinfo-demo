FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/bookinfo-ratings.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms512M","-Xmx512M","-jar","/app.jar"]