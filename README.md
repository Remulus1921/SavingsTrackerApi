# Savings Tracker Api

Savings Tracker is a sophisticated application designed to monitor the value of various assets, including currencies, cryptocurrencies, and precious metals. This application employs robust authentication and authorization mechanisms using JSON Web Tokens (JWT) and supports refresh tokens to ensure secure user sessions. The application retrieves asset values from external APIs, processes the data, and provides real-time and historical tracking of asset values.

## Features

- Secure authentication and authorization using JWT
- Refresh token implementation for continuous authentication
- Real-time tracking of asset values
- Historical analysis of asset value fluctuations
- Integration with multiple external APIs for dynamic data retrieval
- User-friendly interface for easy asset management

## Technologies Used

- Java
- Spring Boot
- Docker
- JWT (JSON Web Token)
- External APIs
- Gradle

## Prerequisites

- Docker installed on your machine
- Java 11 or later
- Gradle

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/Remulus1921/SavingsTrackerApi.git
    cd savings-tracker
    ```

2. Build the application using Gradle:
    ```bash
    ./gradlew build
    ```

3. Build and start the Docker containers using Docker Compose:
    ```bash
    docker-compose up
    ```