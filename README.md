# Lagos-dtupay
Maven 3.5.4 or below is required to build.

In order to compile the code and refresh the docker containers, run 
```
mvn clean install; docker-compose up -d
```
for windows or
```
mvn clean install && docker-compose up -d
```
for mac/linux in the root of the project.

Add 
```
-Dmaven.test.skip=true
```
if you're in a hurry.

In order to run the RPC tests, start a RabbitMQ broker and mongo db:
```
docker run --hostname rabbit1 -p 5672:5672 --name testRabbitmq -d -e "RABBITMQ_DEFAULT_USER=rabbitmq" -e "RABBITMQ_DEFAULT_PASS=rabbitmq" rabbitmq
```
and
```
docker run -p 27017:27017 --name testMongo -d mongo
```
