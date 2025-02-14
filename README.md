# Keycloak OTP Extension

This project provides custom authenticators for Keycloak to enhance its OTP (One-Time Passcode) capabilities. It includes:

- **Phone Number Only Authenticator**: Allows authentication using only a phone number.
- **OTP Authenticator**: Enhances the default OTP authentication mechanism.

## Features

- Customizable authentication flows.
- Integration with external services for OTP delivery.
- Support for various OTP algorithms and configurations.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure you have JDK installed.
- **Apache Maven**: Required for building the project.
- **Docker**: Used for containerizing the application.

## Installation

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/Alireza-Kiani/keycloak-otp-extension.git
   cd keycloak-otp-extension
    ```
2. **Build the Project**:
   ```bash
    mvn clean package -DskipTests
   ```
3. **Build the Project**:
   ```bash
    docker build -t keycloak-otp-extension .
   ```
4. **Build the Project**:
   ```bash
     docker run -p 8080:8080 keycloak-otp-extension
   ```