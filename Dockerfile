FROM openjdk:17-alpine

WORKDIR /app

COPY target/JavaProductParser-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]