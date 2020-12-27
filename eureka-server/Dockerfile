FROM openjdk:11.0.8-jre
ENV SETTINGS="default"
COPY target/eureka-server-*.jar /server.jar
CMD java -Dspring.profiles.active=$SETTINGS -jar /server.jar