package com.ptst.trading.kite.strategy;

import com.ptst.trading.kite.service.MarketDataService;
import com.ptst.trading.kite.util.AppUtils;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 30-Minute Opening Range Breakout (ORB) strategy for NIFTY options.
 *
 * <p><b>Logic:</b>
 *
 * <ol>
 *   <li>At 09:45 IST, fetches the first 30-minute candle's high and low (ORB).
 *   <li>Every 3 minutes thereafter, checks if the spot price breaks the ORB (adjusted by a buffer).
 *   <li>On an upside breakout → sells ATM PUT (PE). On a downside → sells ATM CALL (CE).
 *   <li>Exits when the profit target (1%) is hit or the price reverses back inside the range.
 * </ol>
 *
 * <p>Scheduled via Spring {@link org.springframework.scheduling.annotation.Scheduled} annotations
 * and only runs on weekdays (MON–FRI).
 */
@Component
public class ThirtyMinOrbStrategy {

  private static final Logger log = LoggerFactory.getLogger(ThirtyMinOrbStrategy.class);

  private static boolean rangeInitialized = false;
  private static boolean positionEntered = false;
  private volatile double orbHigh;
  private volatile double orbLow;

  private volatile String positionSide;
  private volatile double entryPremium;
  private volatile String optionTradingSymbol;

  private final MarketDataService<HistoricalData> marketDataService;

  /**
   * Constructs the strategy with a market data service.
   *
   * @param marketDataService service for fetching market data and placing orders
   */
  @Autowired
  public ThirtyMinOrbStrategy(MarketDataService<HistoricalData> marketDataService) {
    this.marketDataService = marketDataService;
  }

  /**
   * Scheduled task that runs at 09:45:05 on weekdays to capture the opening 30-minute candle's high
   * and low as the ORB range.
   *
   * @throws KiteException if fetching historical data fails
   */
  @Scheduled(cron = "5 45 9 * * MON-FRI")
  public void fetchOpeningRange() throws KiteException {
    log.info("=== ThirtyMinOrbScheduler: Fetching opening range ===");

    try {
      HistoricalData result =
          this.marketDataService.get30MinutesOhlc(new Date(), new Date(), AppUtils.NIFTY_SYMBOL);

      HistoricalData candles = result.dataArrayList.getFirst();
      this.orbHigh = candles.high;
      this.orbLow = candles.low;
      rangeInitialized = true;
    } catch (Exception e) {
      log.error("Exception in method fetchOpeningRange {}", e.getMessage());
    }
  }

  /**
   * Scheduled task that runs every 3 minutes (09:45–15:00) on weekdays.
   *
   * <p>Delegates to {@link #checkBreakoutEntry()} if no position is open, or to {@link
   * #monitorPosition()} if a position is active.
   */
  @Scheduled(cron = "5 45/3 9-15 * * MON-FRI") // run every 3 mins after 9:45:05 am
  public void checkConditions() {
    LocalTime now = LocalTime.now(AppUtils.IST);
    if (now.isBefore(LocalTime.of(9, 46)) || now.isAfter(LocalTime.of(15, 0))) {
      return;
    }
    if (!rangeInitialized) {
      return;
    }

    try {
      if (!positionEntered) {
        checkBreakoutEntry();
      } else {
        monitorPosition();
      }
    } catch (Throwable t) {
      log.error("Error in checkConditions", t);
    }
  }

  /**
   * Checks whether the current NIFTY spot price has broken out of the ORB range. On an upside
   * breakout, sells a PUT (PE). On a downside breakout, sells a CALL (CE).
   *
   * @throws KiteException if market data fetching fails
   */
  private void checkBreakoutEntry() throws KiteException {
    try {
      double currentPrice = this.marketDataService.getNiftySpotPrice();
      double breakHigh = orbHigh + AppUtils.BREAKOUT_BUFFER;
      double breakLow = orbLow - AppUtils.BREAKOUT_BUFFER;

      if (currentPrice > breakHigh) {
        log.info("Upside breakout: nifty {} > {}", currentPrice, breakHigh);
        enterTrade("PE");
      } else if (currentPrice < breakLow) {
        log.info("Downside breakout: nifty {} < {}", currentPrice, breakLow);
        enterTrade("CE");
      }
    } catch (Exception e) {
      log.error("Exception in method checkBreakoutEntry", e);
    }
  }

  /**
   * Monitors an open position by checking the current premium against the entry premium. Exits if
   * the profit target is reached or if the spot price reverses back to the ORB boundary.
   *
   * @throws KiteException if market data fetching fails
   * @throws IOException if a network error occurs
   */
  private void monitorPosition() throws KiteException, IOException {
    double currentPremium = this.marketDataService.getOptionPremium(optionTradingSymbol);
    double pnlPercent = (entryPremium - currentPremium) / entryPremium * 100;

    log.info(
        "MONITOR: {} entry={} current={} pnl={}%",
        optionTradingSymbol, entryPremium, currentPremium, String.format("%.2f", pnlPercent));

    if (pnlPercent >= AppUtils.PROFIT_TARGET_PCT) {
      log.info("EXIT SIGNAL: profit target {}}% reached", AppUtils.PROFIT_TARGET_PCT);
      exitPosition("PROFIT_TARGET");
      return;
    }

    double currentNifty = this.marketDataService.getNiftySpotPrice();
    if ("PE".equals(positionSide) && currentNifty <= orbHigh) {
      log.info("EXIT SIGNAL: nifty {} reversed to ORB high {}", currentNifty, orbHigh);
      exitPosition("SL_REVERSAL");
    } else if ("CE".equals(positionSide) && currentNifty >= orbLow) {
      log.info("EXIT SIGNAL: nifty {} reversed to ORB low {}", currentNifty, orbLow);
      exitPosition("SL_REVERSAL");
    }
  }

  /**
   * Closes the current position by placing a buy order and resets state.
   *
   * @param reason the reason for exiting (e.g. "PROFIT_TARGET", "SL_REVERSAL")
   * @throws KiteException if order placement fails
   * @throws IOException if a network error occurs
   */
  private void exitPosition(String reason) throws KiteException, IOException {
    String buyOrderId =
        this.marketDataService.placeBuyOrder(
            optionTradingSymbol, AppUtils.NIFTY_LOT_SIZE * AppUtils.LOT_SIZE);
    log.info("EXIT TRADE: {} reason={} buyOrderId={}", optionTradingSymbol, reason, buyOrderId);
    resetState();
  }

  /**
   * Enters a new trade by selling an ATM option on the detected breakout side.
   *
   * <p>Computes the ATM strike, nearest weekly expiry, builds the option symbol, and places a
   * market sell order.
   *
   * @param side {@code "PE"} for a put (upside breakout) or {@code "CE"} for a call (downside
   *     breakout)
   * @throws KiteException if order placement fails
   * @throws IOException if a network error occurs
   */
  private void enterTrade(String side) throws KiteException, IOException {
    double currentPrice = this.marketDataService.getNiftySpotPrice();
    long atmStrike = roundToNearestStrike(currentPrice);
    String expiry = getNearestExpiry();
    String symbol = buildOptionSymbol(expiry, atmStrike, side);
    double premium = this.marketDataService.getNiftySpotPricePremium();

    String orderId =
        this.marketDataService.placeSellOrder(symbol, AppUtils.NIFTY_LOT_SIZE * AppUtils.LOT_SIZE);

    this.positionSide = side;
    this.entryPremium = premium;
    this.optionTradingSymbol = symbol;
    positionEntered = true;

    log.info(
        "ENTER TRADE: SELL {} premium={} nifty={} orderId={}",
        symbol,
        premium,
        currentPrice,
        orderId);
  }

  /**
   * Rounds a price to the nearest NIFTY option strike (nearest 50).
   *
   * @param price the current NIFTY spot price
   * @return the rounded ATM strike price
   */
  private long roundToNearestStrike(double price) {
    return Math.round(price / 50.0) * 50;
  }

  /**
   * Returns the nearest weekly expiry date formatted as {@code ddMMMyy} (e.g. "02JUN26"). For
   * WED/THU/FRI, returns the next Tuesday; otherwise returns the next or same Tuesday.
   *
   * @return the expiry string in uppercase
   */
  private String getNearestExpiry() {
    LocalDate today = LocalDate.now(AppUtils.IST);
    return switch (today.getDayOfWeek()) {
      case WEDNESDAY, THURSDAY, FRIDAY ->
          today
              .with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
              .format(DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH))
              .toUpperCase();
      default ->
          today
              .with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY))
              .format(DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH))
              .toUpperCase();
    };
  }

  /**
   * Builds a NIFTY option trading symbol in the format {@code NIFTY<expiry><strike><side>}.
   *
   * @param expiry the expiry date string (e.g. "02JUN26")
   * @param strike the strike price
   * @param side {@code "CE"} for call or {@code "PE"} for put
   * @return the full option trading symbol
   */
  private String buildOptionSymbol(String expiry, long strike, String side) {
    return "NIFTY" + expiry + strike + side;
  }

  /** Resets all strategy state variables to defaults, ready for the next trading day's cycle. */
  private void resetState() {
    orbHigh = 0;
    orbLow = 0;
    rangeInitialized = false;
    positionEntered = false;
    positionSide = null;
    entryPremium = 0;
    optionTradingSymbol = null;
  }
}
