# Loan Payment Planner

Loan Payment Planner is a Spring Boot application designed to manage and plan loan payments for Sekerbank.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Run](#run)

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
   cd loan-payment-planner-backend
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
3. Initialize the database:

   ```sh
   psql -U your_username -d your_database -a -f src/main/resources/schema.sql
   ```

## Run

1. Package the project using Maven:
   ```sh
   mvn package
   ```
2. Run the jar file:
   ```sh
   java -jar target/loan-payment-planner-0.0.1-SNAPSHOT.jar
   ```
