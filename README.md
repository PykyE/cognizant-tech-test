# Cognizant Take Home Test

This project contains two main microservices: **tickets-service** and **notifications-service**, plus a Kafka and Zookeeper messaging environment.

## Prerequisites

- Docker and docker compose installed on your machine.
- Java (+17) and Maven if you want to run tests locally outside Docker.

## Running the Full Stack

From the project root, run:

```sh
docker-compose up --build
```

This will start the following services:

- **zookeeper**: Required for Kafka.
- **kafka**: Messaging broker.
- **tickets-service**: Main ticket management microservice (port 8080).
- **notifications-service**: Notification microservice (port 8081).

To stop and remove the containers, run:

```sh
docker-compose down
```

## Running Tests for `tickets-service`

To run unit and integration tests for the `tickets-service` microservice, you have two options:

### 1. Using Maven (recommended)

From the project root or inside the `tickets-service` folder, run:

```sh
cd tickets-service
mvn clean test
```

This will execute all tests defined in the project for both controllerss and services.

### 2. Using Visual Studio Code

If you use VS Code and have the Java environment set up, you can open the `tickets-service` folder and run the tests directly from the UI (test explorer panel).