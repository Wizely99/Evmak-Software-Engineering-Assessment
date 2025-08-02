

# Deployment Guide for Client Portal Spring App

This guide provides instructions for deploying the Spring Boot application using Maven and Docker Compose.

## Prerequisites

Ensure you have the following installed on your machine:

- **Java 21** or later
- **Maven**
- **Docker** and **Docker Compose**

---

## 1. Build the Spring Boot Application

First, ensure you have all the necessary dependencies and project setup:

### a. Clone the repository :

```bash
git clone https://hpaul1@bitbucket.org/aimgrouptech/client-portal-api.git
cd client-parking-api
```

### b. Clean, compile, and package the application:

```bash
sudo mvn clean package -DskipTests
```
This command will:

- Clean any previously compiled files.
- Compile the application and its dependencies.
- Package the application into a JAR file, which will be located in the target/ directory (e.g., target/client-management-portal.jar).

---

## 2. Build and Deploy with Docker Compose


### a. Clean, compile, and package the application:

Once the Spring Boot application is built with Maven, you can proceed to build the Docker containers. Run the following command in the root of your project (where `docker-compose.yml` is located):

```bash
sudo docker compose up --build -d
```

This command will:

- Build the images defined in the `docker-compose.yml` file.
- Start the containers in detached mode (`-d`).

The `client-portal-app`, `client-portal-db`, and `client-portal-redis` containers will be created, and the application will be ready to run.

---

## 3. Access the Application

Once the Docker containers are up, you can access the Spring Boot application via the URL:

```
http://localhost:8888/swagger-ui/index.html
```

### Changing Ports

If you need to change the ports for the application, you can modify the `docker-compose.yml` file.

For example, to change the host port for the Spring Boot application, update the following in `docker-compose.yml`:

```yaml
ports:
  - "8888:8080"  # Change 8888 to your preferred host port
```
---

## 4. Stop the Application

To stop the running containers, use:

```bash
sudo docker compose down
```

This will stop and remove all containers, networks, and volumes created by the `docker-compose up` command.

---

## 5. Logs and Troubleshooting

If you encounter any issues or need to check the logs, you can use the following command to view the logs of the Spring Boot application container:

```bash
sudo docker logs client-parking-app --tail 50
```

This will display the last 50 lines of the application logs.

---

## 6. Additional Notes

- The application is configured to run on the `prod` profile in Docker. If you need to change the active Spring profile, update the `SPRING_PROFILES_ACTIVE` environment variable in the `docker-compose.yml` file.

- Ensure that your PostgresSQL database (`client-portal-db`) and Redis (`client-portal-redis`) are accessible from the application. These services are linked through the Docker network `client-portal-net`.

---
#### Notes:
    - Remember to change the value in environment for production
---
#### TODO :
    - Use .env to manage environment variables
---