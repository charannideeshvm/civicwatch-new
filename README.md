# CivicWatch+

A college-level web application for reporting and monitoring public infrastructure complaints.

## Architecture & Concepts

- **Framework**: Spring Boot 3 & Java 21
- **Database**: MySQL with Spring Data JPA (Hibernate)
- **Frontend**: HTML/CSS/Vanilla JS with Bootstrap and Thymeleaf templating
- **OOP Concepts**:
  - Encapsulation: Models (`User`, `Complaint`) with private fields and getters/setters.
  - Abstraction: Repositories and Services abstract away database logic from the controller.
  - Role-based Enums (`Role.CITIZEN`, `Role.OFFICER`, `Role.ADMIN`).

## Requirements
- Java 21
- Maven
- MySQL Server

## Setup Instructions

1. **Database Configuration**
   Ensure MySQL is running on `localhost:3306`.
   The database `civicwatch` will be created automatically if it doesn't exist, as configured in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/civicwatch?createDatabaseIfNotExist=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   ```
   (Update the username/password in `src/main/resources/application.properties` if yours are different).

2. **Build the Application**
   Run the following command in the project root:
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the App**
   Open your browser and navigate to: `http://localhost:8080`

## Demo Credentials
The application seeds demo users on startup (if the users table is empty).
- **Citizen**: `citizen@civicwatch.com` / `citizen123`
- **Officer**: `officer@civicwatch.com` / `officer123`
- **Admin**: `admin@civicwatch.com` / `admin123`

## Known Limitations
- The Priority Calculation assumes fixed environmental factors (density, location risk) to keep the project explainable without external APIs.
- Uploaded images are stored on the local file system (`uploads/` folder).
- Map interactions rely on browser geolocation or manual map pinning; no Google Maps API is used to avoid costs.
