# Kite API Project

This is a Spring Boot REST API to connect to Zerodha Kite and retrieve active positions, built with SOLID principles and clean code standards.

## Architecture

The project follows a decoupled architecture using interfaces and focused services:

- **KiteSessionManager**: Manages the Kite session, including the access token lifecycle and authentication.
- **KitePositionService**: Specialized service for position-related operations.
- **GlobalExceptionHandler**: Centralized error handling for the entire API.
- **KiteController**: Lean REST controller that delegates business logic to specialized services.

## Setup

1.  Create a `.env` file in the `kiteapi` directory (a template `.env` is already provided).
2.  Fill in your Kite API credentials:
    - `KITE_API_KEY`
    - `KITE_API_SECRET`
    - `KITE_USER_ID`
3.  Obtain an `access_token` from Kite (usually by logging in via the login URL and exchanging the `request_token`).
4.  Set the `KITE_ACCESS_TOKEN` in the `.env` file or use the `/api/access-token` endpoint.

## API Endpoints

### Get Login URL
- **URL:** `/api/login-url`
- **Method:** `GET`
- **Description:** Returns the Kite login URL to obtain a `request_token`.

### Get Active Positions
- **URL:** `/api/positions`
- **Method:** `GET`
- **Description:** Returns the list of active net positions.

### Update Access Token
- **URL:** `/api/access-token`
- **Method:** `POST`
- **Body:** Plain text access token
- **Description:** Updates the access token in memory for the current session.

## Building and Running

To build the project:
```bash
./gradlew :kiteapi:build
```

To run the application:
```bash
./gradlew :kiteapi:bootRun
```
