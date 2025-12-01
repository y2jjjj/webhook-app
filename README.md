# Bajaj Finserv Health - Webhook Application

This Spring Boot application automatically generates a webhook on startup, solves a SQL problem, and submits the solution.

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Configuration
1. Update your details in `WebhookService.java`:
   - Replace `name`, `regNo`, and `email` with your actual details
   - Add your SQL query in the `getSqlQuery()` method

### Build the Application
```bash
mvn clean package
```

The JAR file will be created in the `target/` directory as `webhook-app.jar`

### Run the Application
```bash
java -jar target/webhook-app.jar
```

The application will automatically:
1. Generate a webhook on startup
2. Solve the assigned SQL problem
3. Submit the solution to the webhook URL

## Project Structure
```
src/main/java/com/bfhl/webhookapp/
├── WebhookApplication.java          # Main application class
├── config/
│   └── AppConfig.java               # RestTemplate configuration
├── model/
│   ├── WebhookRequest.java          # Request model for webhook generation
│   ├── WebhookResponse.java         # Response model from webhook
│   └── SolutionRequest.java         # Request model for solution submission
└── service/
    └── WebhookService.java          # Main service with startup logic
```

## Downloadable JAR Link
After building, upload the JAR file from `target/webhook-app.jar` to your GitHub repository and provide the raw link:
```
https://github.com/your-username/your-repo/raw/main/target/webhook-app.jar
```

## Submission
- GitHub Repository: https://github.com/your-username/your-repo.git
- JAR Download Link: https://github.com/your-username/your-repo/raw/main/target/webhook-app.jar
