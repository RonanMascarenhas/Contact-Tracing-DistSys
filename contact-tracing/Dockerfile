FROM openjdk:11.0.8-jre
COPY target/contact-tracing-*.jar /service.jar
ENV SETTINGS = "docker"
CMD java -Dspring.profiles.active=docker -jar /service.jar