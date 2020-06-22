FROM openjdk:14-jdk-slim
EXPOSE 8080
ADD /target/cambiosearch-0.0.1-SNAPSHOT.jar cambiosearch.jar
ENTRYPOINT ["java","-jar","/cambiosearch.jar"]