BlackJack Game

A web-based BlackJack game built by Cameron Sprouse using a Java backend and React frontend.

Technologies
Java 25
Spring
Maven 
React (npm not needed)


Requirements
Java 25
Maven

Running the project
Java
mvn clean install
mvn spring-boot:run

Docker
docker build -t blackjack .
docker run -p 8080:8080 blackjack

Running the Frontend at
http://localhost:8080/



Description
The game supports up to five players in a turn-based Blackjack experience with accurate game flow and state management. Thread-safe operations are used where needed to ensure consistent game state across concurrent actions. Each round is represented as a Java object, serialized to JSON, and transmitted to the React frontend through WebSockets for real-time rendering and updates.

Author
Cameron Sprouse