# Conway's Game of Life
A Java 1.8 Spring Boot implementation of Conway's Game of Life.

## Build application
Run the following Maven goals:
```
mvn clean install
```

## Run application
Execute the following command in the same directory (target) that the JAR file resides in:
```
java -jar conways-game-of-life.jar
```

By default, the application runs on port 8080. If you already have something running on this port you can add *server.port=8081* to the `application.properties` file. You can also execute the JAR with a JVM argument to change the port as shown below.
```
java -jar -Dserver.port=8081 conways-game-of-life.jar
```
If you import the code into an IDE you can run the application by finding the `ConwaysGameOfLifeApplication.java` file, and running it as a Java application.
