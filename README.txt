Blackjack Game

A web-based Blackjack game built by Cameron Sprouse using a Java backend and React frontend.

Technologies
Java 25
Spring
Maven
React (npm is not needed)


Requirements
Java 25
Maven

Running the project
Run these commands from the project root directory, where the pom.xml file is located:
mvn clean install
mvn spring-boot:run


Running the frontend
http://localhost:8080/

Playing the game
Once the game loads, you can choose any ID and username.
Use the same ID and username to log back in as the same user.


Install and Run issues
To confirm that Maven and Java are using the correct versions:
java --version
Confirm that the output includes "openjdk 25."

mvn --version
Confirm that the output includes "Java version: 25."

If there is a generic "BUILD FAILURE" message:
Confirm that the commands are run from the project root directory.

Author
Cameron Sprouse