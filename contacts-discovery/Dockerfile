FROM openjdk:11.0.8-jre
COPY target/contacts-discovery-*.jar /ContactsDiscovery.jar
ENV SETTINGS = "docker"
CMD java -Dspring.profiles.active=docker -jar  /ContactsDiscovery.jar
