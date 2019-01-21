FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENV BROKER_HOST_NAME=localhost
ENV BROKER_HOST_USERNAME=guest
ENV BROKER_HOST_PASSWORD=guest
ENTRYPOINT java -Drabbit.host=$BROKER_HOST_NAME \
            -Drabbit.username=$BROKER_HOST_USERNAME \
            -Drabbit.password=$BROKER_HOST_PASSWORD \
            -Djava.net.preferIPv4Stack=true \
            -Djava.net.preferIPv4Addresses=true \
            -jar /app.jar