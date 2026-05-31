package com.ptst.trading.kite.util;

/**
 * Standardized log message codes for consistent structured logging across the application. Each
 * code represents a distinct event type and maps to a human-readable template.
 *
 * <p>Usage:
 *
 * <pre>{@code log.info(LogCodes.SESSION_LOGIN_SUCCESS + " — user={}", userId);}</pre>
 */
public final class LogCodes {

  private LogCodes() {
    throw new AssertionError("Utility class");
  }

  // ──────────────────────────────────────────────
  // Session & Authentication (KITE-SESS-*)
  // ──────────────────────────────────────────────
  public static final String SESSION_LOGIN_URL_GENERATED = "[KITE-SESS-001] Login URL generated";
  public static final String SESSION_TOKEN_SET = "[KITE-SESS-002] Access token set";
  public static final String SESSION_TOKEN_MISSING =
      "[KITE-SESS-003] Access token is missing — authenticate first";
  public static final String SESSION_CALLBACK_STARTED =
      "[KITE-SESS-004] OAuth callback received — exchanging request token";
  public static final String SESSION_CALLBACK_SUCCESS =
      "[KITE-SESS-005] OAuth callback succeeded — access token obtained";
  public static final String SESSION_CALLBACK_FAILED = "[KITE-SESS-006] OAuth callback failed";

  // ──────────────────────────────────────────────
  // Market Data (KITE-MD-*)
  // ──────────────────────────────────────────────
  public static final String MD_HISTORICAL_FETCH =
      "[KITE-MD-001] Fetching historical data — interval={}, symbol={}";
  public static final String MD_SPOT_PRICE_FETCH = "[KITE-MD-002] Fetching NIFTY spot price";
  public static final String MD_OPTION_PREMIUM_FETCH =
      "[KITE-MD-003] Fetching option premium — symbol={}";
  public static final String MD_TOKEN_RESOLVED =
      "[KITE-MD-004] Instrument token resolved — symbol={}, token={}";
  public static final String MD_TOKEN_NOT_FOUND = "[KITE-MD-005] Instrument not found — symbol={}";

  // ──────────────────────────────────────────────
  // Orders (KITE-ORD-*)
  // ──────────────────────────────────────────────
  public static final String ORDER_SELL_PLACED =
      "[KITE-ORD-001] Sell order placed — symbol={}, qty={}, orderId={}";
  public static final String ORDER_BUY_PLACED =
      "[KITE-ORD-002] Buy order placed — symbol={}, qty={}, orderId={}";
  public static final String ORDER_SELL_FAILED =
      "[KITE-ORD-003] Sell order failed — symbol={}, qty={}";
  public static final String ORDER_BUY_FAILED =
      "[KITE-ORD-004] Buy order failed — symbol={}, qty={}";

  // ──────────────────────────────────────────────
  // Positions (KITE-POS-*)
  // ──────────────────────────────────────────────
  public static final String POS_FETCHING = "[KITE-POS-001] Fetching net positions";
  public static final String POS_FETCHED = "[KITE-POS-002] Net positions fetched — count={}";

  // ──────────────────────────────────────────────
  // Strategy – ORB (STRAT-ORB-*)
  // ──────────────────────────────────────────────
  public static final String ORB_RANGE_SET = "[STRAT-ORB-001] Opening range set — high={}, low={}";
  public static final String ORB_BREAKOUT_UP =
      "[STRAT-ORB-002] Upside breakout detected — price={}, breakHigh={}";
  public static final String ORB_BREAKOUT_DOWN =
      "[STRAT-ORB-003] Downside breakout detected — price={}, breakLow={}";
  public static final String ORB_ENTER_TRADE =
      "[STRAT-ORB-004] Entering trade — side={}, symbol={}, premium={}, orderId={}";
  public static final String ORB_EXIT_TRADE =
      "[STRAT-ORB-005] Exiting trade — symbol={}, reason={}, orderId={}";
  public static final String ORB_MONITOR =
      "[STRAT-ORB-006] Monitoring position — symbol={}, entry={}, current={}, pnl={}%";
  public static final String ORB_PROFIT_TARGET =
      "[STRAT-ORB-007] Profit target reached — target={}%";
  public static final String ORB_SL_REVERSAL =
      "[STRAT-ORB-008] Stop-loss reversal triggered — nifty={}, orbBoundary={}";

  // ──────────────────────────────────────────────
  // System / General (SYS-*)
  // ──────────────────────────────────────────────
  public static final String SYS_APP_STARTED = "[SYS-001] Application started";
  public static final String SYS_APP_STOPPING = "[SYS-002] Application is shutting down";
  public static final String SYS_SCHEDULED_TASK_START =
      "[SYS-003] Scheduled task started — task={}";
  public static final String SYS_SCHEDULED_TASK_ERROR = "[SYS-004] Scheduled task failed — task={}";
  public static final String SYS_UNEXPECTED_ERROR = "[SYS-005] Unexpected error";
}
