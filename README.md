# Planning Poker

Planning Poker is a collaborative estimation tool designed to help agile teams estimate effort for tasks and stories.  
This project was developed by François Quinaou, Tommi Lucas, and Nicolas Baron.

## Table of Contents

- [Project Overview](#project-overview)
- [Required Tools](#required-tools)
- [Configuration & Setup](#configuration--setup)
- [Running the Application](#running-the-application)
- [Deployment Notes](#deployment-notes)
- [Contributors](#contributors)

## Project Overview

The application consists of:
- **Frontend**: Svelte (JavaScript/TypeScript) with TailwindCSS for design
- **Backend**: Java (Spring Boot, built with Maven)
- **Database**: PostgreSQL (via Docker)

## Required Tools

To develop and run Planning Poker locally, you will need:

- [Node.js](https://nodejs.org/) (for the frontend)
- [npm](https://www.npmjs.com/) (for frontend dependencies)
- [Java 17+](https://adoptopenjdk.net/) (for the backend)
- [Maven](https://maven.apache.org/) (for backend build and run)
- [Docker](https://www.docker.com/) (for the database)
- [Git](https://git-scm.com/) (for source code management)

## Configuration & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/ChichEnBalle/planning-poker.git
cd planning-poker
```

### 2. Database Setup

The repository already includes a `docker-compose.yml` file to run the PostgreSQL database.  
You only need to start the database container:

```bash
docker-compose up -d db
```

By default, the configuration is:
- **Host:** localhost
- **Port:** 5432
- **Database:** planningpoker
- **User:** pokeruser
- **Password:** pokerpass

Make sure your backend configuration matches these credentials (usually set in `src/main/resources/application.properties`):

```
spring.datasource.url=jdbc:postgresql://localhost:5432/planningpoker
spring.datasource.username=pokeruser
spring.datasource.password=pokerpass
```

### 3. Backend Setup

1. Navigate to the backend directory (e.g., `backend/`):

    ```bash
    cd backend
    ```

2. Run the backend with Maven and Spring Boot:

    ```bash
    mvn spring-boot:run
    ```

### 4. Frontend Setup

1. Navigate to the frontend directory (e.g., `frontend/`):

    ```bash
    cd frontend
    ```

2. Install dependencies:

    ```bash
    npm install
    ```

    > **Note:** TailwindCSS is already configured as part of the frontend dependencies.

3. Start the frontend development server:

    ```bash
    npm run dev
    ```

### 5. Access the Application

- The frontend is usually available at [http://localhost:5173](http://localhost:5173)
- The backend API (Spring Boot) is accessible at [http://localhost:8080](http://localhost:8080)

## Deployment Notes

- The project was intended to be deployed entirely with Docker Compose, but due to issues with WebSocket compatibility and Docker Compose, only the database is run with Docker.
- **Current Workflow:**  
  - Start the database container with Docker Compose.
  - Run the backend in one terminal.
  - Run the frontend in a separate terminal.
- Full Docker Compose deployment (frontend + backend + db) is **not supported** at this time.

## Contributors

- François Quinaou
- Tommi Lucas
- Nicolas Baron
