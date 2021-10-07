FROM openjdk:11-jre-slim
LABEL maintainer="jehyn923@gmail.com"
VOLUME /tmp
ARG JAR_FILE=./build/libs/*.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8085
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://3.36.52.243/?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false","-Djava.security.egd=file:/dev/./uradom","-jar","/app.jar"]