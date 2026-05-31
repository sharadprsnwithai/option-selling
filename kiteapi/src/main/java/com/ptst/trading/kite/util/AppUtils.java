package com.ptst.trading.kite.util;

import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central constants and utility values shared across the trading application.
 *
 * <p>Defines breakout parameters, exchange identifiers, instrument symbols, lot sizes, and the
 * Indian Standard Time (IST) time zone.
 */
public final class AppUtils {

  private AppUtils() {
    throw new AssertionError("Utility class");
  }

  private static final Logger log = LoggerFactory.getLogger(AppUtils.class);

  /** Buffer added to ORB high/low for breakout confirmation (in points). */
  public static final int BREAKOUT_BUFFER = 5;

  /** Profit target percentage before exiting a position. */
  public static final double PROFIT_TARGET_PCT = .6;

  /** National Stock Exchange identifier. */
  public static final String NSE = "NSE";

  /** NSE Futures & Options segment identifier. */
  public static final String NFO = "NFO";

  /** Trading symbol for the NIFTY 50 index. */
  public static final String NIFTY_SYMBOL = "NIFTY 50";

  /** Lot size multiplier for NIFTY options. */
  public static final int NIFTY_LOT_SIZE = 65;

  /** Number of lots to trade per position. */
  public static final int LOT_SIZE = 5;

  /** Indian Standard Time zone (Asia/Kolkata). */
  public static final ZoneId IST = ZoneId.of("Asia/Kolkata");
}
