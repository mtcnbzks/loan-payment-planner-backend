# Loan Payment Planner

Loan Payment Planner is a Spring Boot application designed to manage and plan loan payments for Sekerbank.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)

## Getting Started

These instructions will help you set up and run the project on your local machine for development and testing purposes.

## Prerequisites

- Java 21
- Maven
- PostgreSQL

## Installation

1. Clone the repository:

   ```sh
   git clone <repository-url>
   cd loan-payment-planner
   ```

2. Configure the PostgreSQL database in `src/main/resources/application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/your_database
       username: your_username
       password: your_password
     jpa:
       hibernate:
         ddl-auto: update
   ```

3. Build the project using Maven:
   ```sh
   mvn clean install
   ```

## Running the Application

To run the application, use the following command:

```sh
mvn spring-boot:run
```

The application will start on http://localhost:8080.
