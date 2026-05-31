# Kite API — Option Selling Trading Platform

Spring Boot REST API for Zerodha Kite Connect integration. Handles OAuth authentication, session management, market data, position retrieval, and automated 30-minute Opening Range Breakout (ORB) strategy for algorithmic option selling.

## Architecture

The project follows a decoupled interface/implementation pattern:

### Session & Authentication
- **KiteSessionManager** (interface) / **KiteSessionManagerImpl** — OAuth flow: login URL generation, `generateSession()` request token exchange, in-memory access token storage with optional pre-configured token for restart persistence.

### Market Data & Orders
- **MarketDataService\<T\>** (interface) / **KiteMarketDataServiceImpl** — Fetches historical OHLC data (3-min, 30-min), real-time NIFTY spot quotes, option premiums, and places MARKET buy/sell orders on NFO. Caches instrument→token mappings.

### Positions
- **KitePositionService** (interface) / **KitePositionServiceImpl** — Retrieves net positions via `KiteConnect.getPositions()`. Authenticates session before each API call.

### Strategy
- **ThirtyMinOrbStrategy** — Scheduled component implementing the 30-min ORB strategy:
  - `09:45 IST` — Captures opening 30-min candle range
  - Every 3 mins — Checks breakout beyond ORB + 5pt buffer
  - Upside breakout → sell ATM PUT; Downside → sell ATM CALL
  - Exits at 1% profit target or range reversal
- **Strategy** (interface) — Core strategy contract with lifecycle (init, enter, exit, adjust, hedge, signal, position sizing, risk, validation)
- **Signal** — `STRONG_BUY` / `BUY` / `HOLD` / `SELL` / `STRONG_SELL`
- **StrategyStatus** — `DRAFT` / `ACTIVE` / `PAUSED` / `STOPPED` / `COMPLETED`
- **TradeAction** — `ENTER` / `EXIT` / `ADJUST` / `HEDGE` / `ROLL` / `DO_NOTHING`

### Common
- **KiteConfig** — Creates `KiteConnect` bean from config properties
- **KiteController** — Lean REST controller (`/api/kite/*`) delegating to services
- **ApiResponse\<T\>** — Generic response wrapper (`success`, `message`, `data`, `timestamp`)
- **GlobalExceptionHandler** — Maps `KiteException` / `IOException` / `IllegalStateException` / `Exception` to structured error responses
- **AppUtils** — Central constants (buffer, profit target, exchange IDs, NIFTY symbol, lot sizes, IST)
- **LogCodes** — 25 standardized log codes with prefixes (`KITE-SESS`, `KITE-MD`, `KITE-ORD`, `KITE-POS`, `STRAT-ORB`, `SYS`)

## Setup

1. Create a `.env` file in the `kiteapi` directory.
2. Fill in your Kite API credentials:
   - `KITE_API_KEY`
   - `KITE_API_SECRET`
   - `KITE_USER_ID`
3. Obtain an `access_token` from Kite (login via the login URL and exchange the `request_token`).
4. Set `KITE_ACCESS_TOKEN` in `.env` or use `POST /api/kite/access-token`.

## API Endpoints

All endpoints are under `/api/kite`.

### `GET /api/kite/login`
Returns the Kite login URL to obtain a `request_token`.

### `GET /api/kite/callback?request_token=xxx`
OAuth callback — exchanges `request_token` for a permanent access token.

### `POST /api/kite/access-token`
Manually sets an access token in memory (plain text body).

### `GET /api/kite/positions`
Returns net positions from the Kite account (requires valid access token).

## Building and Running

```bash
# Build (skip tests)
./gradlew :kiteapi:build -x test

# Run
./gradlew :kiteapi:bootRun

# Run tests
./gradlew :kiteapi:test

# Format code (Google Java Format)
./gradlew spotlessApply

# Check dependency updates
./gradlew dependencyUpdates

# Docker
docker build -t kiteapi .
docker run -p 8080:8080 --env-file kiteapi/.env kiteapi
```
