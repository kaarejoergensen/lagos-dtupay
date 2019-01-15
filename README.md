# Lagos-dtupay
Maven 3.5.4 or below is required to build.

In order to compile the code and refresh the docker containers, run 
```
mvn clean install; mvn -f dtupay-api/ dockerfile:build; docker-compose up -d
```
for windows or
```
mvn clean install && mvn -f dtupay-api/ dockerfile:build && docker-compose up -d
```
for mac/linux in the root of the project.

Add 
```
-Dmaven.test.skip=true
```
if you're in a hurry.
