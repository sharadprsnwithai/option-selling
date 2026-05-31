# Kite API — Option Selling Trading Platform

Spring Boot REST API for Zerodha Kite Connect integration. Handles OAuth authentication, session management, market data, position retrieval, and automated 30-minute Opening Range Breakout (ORB) strategy for algorithmic option selling.

## Tech Stack

- **Java 21** — Language
- **Spring Boot 3.4.4** — Framework
- **Kite Connect 4.0.0** — Zerodha trading API
- **Gradle 9.4.1** — Build tool
- **JUnit 5 + Mockito** — Testing
- **Spotless + Google Java Format** — Code formatting
- **Gradle Versions Plugin** — Dependency version checking

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

# Format code (Google Java Format)
./gradlew spotlessApply

# Check formatting only
./gradlew spotlessCheck

# Check for dependency updates
./gradlew dependencyUpdates
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
│   │   │   ├── KiteApiApplication.java              # Entry point
│   │   │   ├── config/
│   │   │   │   └── KiteConfig.java                   # KiteConnect bean
│   │   │   ├── controller/
│   │   │   │   └── KiteController.java               # REST endpoints
│   │   │   ├── dto/
│   │   │   │   └── ApiResponse.java                  # Standard response wrapper
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java        # Global error handling
│   │   │   ├── service/
│   │   │   │   ├── KitePositionService.java           # Position service interface
│   │   │   │   ├── KitePositionServiceImpl.java       # Position retrieval logic
│   │   │   │   ├── KiteSessionManager.java            # Session interface
│   │   │   │   ├── KiteSessionManagerImpl.java        # OAuth + token management
│   │   │   │   ├── MarketDataService.java             # Market data & orders interface
│   │   │   │   └── KiteMarketDataServiceImpl.java     # Market data & orders impl
│   │   │   ├── strategy/
│   │   │   │   └── ThirtyMinOrbStrategy.java          # 30-min ORB strategy
│   │   │   └── util/
│   │   │       └── AppUtils.java                      # Shared constants
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

strategy/
├── src/main/java/com/ptst/trading/strategy/
│   ├── Strategy.java                                  # Core strategy interface
│   └── model/
│       ├── Signal.java                                # Trading signal enum
│       ├── StrategyStatus.java                        # Strategy lifecycle enum
│       └── TradeAction.java                           # Trade action enum
└── build.gradle
```

## OAuth Flow

1. Call `GET /api/kite/login` to get the login URL
2. Open the URL in a browser, log in to Zerodha
3. Kite redirects to the configured redirect URL with a `request_token`
4. `GET /api/kite/callback?request_token=xxx` exchanges it for an access token
5. The access token is stored in memory and used for subsequent API calls
6. Optionally preserve the token and set it via environment variable `KITE_ACCESS_TOKEN` on restart

## 30-Minute ORB Strategy

The `ThirtyMinOrbStrategy` is a scheduled component that:

1. **09:45 IST** — Fetches the opening 30-minute candle to establish the ORB range (high & low).
2. **Every 3 min (09:45–15:00)** — Monitors the NIFTY spot price for a breakout beyond the ORB range (plus a 5-point buffer).
3. **Entry** — On upside breakout: sell ATM Put (PE). On downside breakout: sell ATM Call (CE). Quantity = 65 lot size × 5 lots.
4. **Exit** — Exits at 1% profit target or if the spot price reverses back inside the range.

## Code Formatting

This project uses **Spotless** with **Google Java Format** to enforce consistent code style.

```bash
# Apply formatting to all Java files
./gradlew spotlessApply

# Verify formatting (CI check)
./gradlew spotlessCheck
```

## Dependency Updates

The **Gradle Versions Plugin** (`com.github.ben-manes.versions`) helps keep dependencies current.

```bash
# Check for available updates (excludes alpha/beta/rc pre-release versions)
./gradlew dependencyUpdates
```
