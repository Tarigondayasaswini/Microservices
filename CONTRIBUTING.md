# Contributing to RevPlay Microservices

Welcome to the newly refactored RevPlay microservices platform!

## Repository Strategy

For testing purposes, all code currently resides in this monorepo. 
In a production setting, this repository should be split into **Polyrepo** architecture.
Each service folder (e.g., `revplay-user-service`, `revplay-catalog-service`) will be its own Git repository.

## Branching Model

We follow **GitHub Flow**:
1. Main branch (`main`) is always deployable.
2. Create a feature branch (`feature/description`) from `main`.
3. Commit your changes locally.
4. Open a Pull Request against `main`.
5. Require at least one code review and passing CI before merging.

## Running Locally

To run the entire suite locally:
1. Ensure Docker Desktop is running.
2. From the root directory, run: `docker-compose up -d --build`
3. The API Gateway will be available at `http://localhost:8080`

### Local Development Tips
- You can run infrastructure services (MySQL, Config, Discovery) via docker-compose, and run the specific microservice you are developing directly via your IDE to allow hot-reloading and easier debugging.

## CI/CD Pipeline

Once migrated to individual repositories, each repo will have its own GitHub Actions or Jenkins pipeline containing:
1. **Compilation & Unit Tests**: `mvn clean test`
2. **Quality Gates**: SonarQube static analysis
3. **Docker Build**: Build the image and push to ECR/DockerHub
4. **Deploy**: Update the deployment manifest in the Kubernetes repo and apply.

## Cross-Cutting Concerns
- **Authentication**: JWT is validated at the gateway. Downstream services receive the user ID via headers (if needed) or validate the passed JWT token directly.
- **Errors**: Return standard `ApiResponse` objects across all services.
