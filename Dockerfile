FROM eclipse-temurin:17-jdk
# For China docker endpoint
# FROM docker-0.unsee.tech/eclipse-temurin:17-jdk
VOLUME /tmp
ENV DB_HOST=mysql
ENV REDIS_HOST=redis
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]