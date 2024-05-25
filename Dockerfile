FROM openjdk:21
LABEL authors="Nadege"
MAINTAINER m1miage.cybersparkle
EXPOSE 1337
COPY target/S8_projet_reseau-1.0-SNAPSHOT-client.jar client.jar
ENTRYPOINT ["java","-jar","/client.jar"]