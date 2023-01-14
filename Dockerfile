#FROM openjdk:17-jdk-alpine
FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=target/*.jar

RUN addgroup -S spring && adduser -S spring -G spring

COPY ${JAR_FILE} /app/app.jar
WORKDIR /app

RUN chown -R spring:spring /app
RUN chmod 755 /app

USER spring:spring

ENTRYPOINT ["java","-jar","/app/app.jar"]
#COPY docker/run.sh run.sh
#ENTRYPOINT ["/run.sh"]
#CMD ["java -jar /app.jar"]