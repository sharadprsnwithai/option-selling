# Kite API — Option Selling Trading Platform

Spring Boot REST API for Zerodha Kite Connect integration. Handles OAuth authentication, session management, and position retrieval for algorithmic option selling strategies.

## Tech Stack

- **Java 21** — Language
- **Spring Boot 3.4.4** — Framework
- **Kite Connect 4.0.0** — Zerodha trading API
- **Gradle 9.4.1** — Build tool
- **JUnit 5 + Mockito** — Testing

## Prerequisites

- JDK 21+
- Zerodha Kite account with API access enabled
- Kite API key and secret from [Kite Developers](https://developers.kite.trade/)

## Configuration

Configure via environment variables or `.env` file in the `kiteapi/` directory:

| Variable | Description | Default |
|---|---|---|
| `KITE_API_KEY` | Kite Connect API key | `your_api_key` |
| `KITE_API_SECRET` | Kite Connect API secret | `your_api_secret` |
| `KITE_USER_ID` | Zerodha user ID | `your_user_id` |
| `KITE_ACCESS_TOKEN` | Pre-existing access token (optional) | _(empty)_ |
| `KITE_REDIRECT_URL` | OAuth callback URL (must match Kite developer console) | `http://localhost:8080/api/kite/callback` |

Copy `kiteapi/.env.example` to `kiteapi/.env` and fill in your credentials.

## Build & Run

```bash
# Build
./gradlew :kiteapi:build

# Run
./gradlew :kiteapi:bootRun

# Run tests
./gradlew :kiteapi:test
```

## API Endpoints

All endpoints are under `/api/kite`.

### `GET /api/kite/login`

Returns the Kite Connect login URL. Open this in a browser to authenticate.

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": "https://kite.zerodha.com/connect/login?api_key=...&v=3",
  "timestamp": "2026-05-30T12:00:00Z"
}
```

---

### `GET /api/kite/callback?request_token=xxx`

OAuth callback endpoint. Kite redirects here after login with a `request_token`. The token is exchanged for an access token via `generateSession()`.

**Response (success):**
```json
{
  "success": true,
  "message": "Authentication successful",
  "data": "your_access_token_here",
  "timestamp": "2026-05-30T12:00:00Z"
}
```

**Response (failure):**
```json
{
  "success": false,
  "message": "Kite API error: ...",
  "data": null,
  "timestamp": "2026-05-30T12:00:00Z"
}
```

---

### `POST /api/kite/access-token`

Manually set an access token (alternative to OAuth callback).

**Request body:** `your_access_token_here` (plain text)

**Response:**
```json
{
  "success": true,
  "message": "Access token updated successfully",
  "data": null,
  "timestamp": "2026-05-30T12:00:00Z"
}
```

---

### `GET /api/kite/positions`

Retrieve net positions from the Kite trading account. Requires a valid access token to be set first.

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "tradingSymbol": "NIFTY",
      "quantity": 50,
      "buyQuantity": 100,
      "sellQuantity": 50,
      "netQuantity": 50,
      "pnl": 1250.00
    }
  ],
  "timestamp": "2026-05-30T12:00:00Z"
}
```

## Project Structure

```
kiteapi/
├── src/
│   ├── main/
│   │   ├── java/com/ptst/trading/kite/
│   │   │   ├── KiteApiApplication.java          # Entry point
│   │   │   ├── config/
│   │   │   │   └── KiteConfig.java               # KiteConnect bean
│   │   │   ├── controller/
│   │   │   │   └── KiteController.java           # REST endpoints
│   │   │   ├── dto/
│   │   │   │   └── ApiResponse.java              # Standard response wrapper
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java    # Global error handling
│   │   │   └── service/
│   │   │       ├── KitePositionService.java       # Position service interface
│   │   │       ├── KitePositionServiceImpl.java   # Position retrieval logic
│   │   │       ├── KiteSessionManager.java        # Session interface
│   │   │       └── KiteSessionManagerImpl.java    # OAuth + token management
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ptst/trading/kite/
│           ├── controller/
│           │   └── KiteControllerTest.java
│           ├── dto/
│           │   └── ApiResponseTest.java
│           ├── exception/
│           │   └── GlobalExceptionHandlerTest.java
│           └── service/
│               ├── KitePositionServiceImplTest.java
│               └── KiteSessionManagerImplTest.java
├── .env
└── build.gradle
```

## OAuth Flow

1. Call `GET /api/kite/login` to get the login URL
2. Open the URL in a browser, log in to Zerodha
3. Kite redirects to the configured redirect URL with a `request_token`
4. `GET /api/kite/callback?request_token=xxx` exchanges it for an access token
5. The access token is stored in memory and used for subsequent API calls
6. Optionally preserve the token and set it via environment variable `KITE_ACCESS_TOKEN` on restart
