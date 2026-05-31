package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import java.io.IOException;
import java.util.Date;

/**
 * Service interface for fetching market data from Kite Connect and placing orders on the exchange.
 *
 * @param <T> the type of historical data result (e.g. {@link
 *     com.zerodhatech.models.HistoricalData})
 */
public interface MarketDataService<T> {

  /**
   * Retrieves 30-minute OHLC candles for the given symbol and date range.
   *
   * @param startDate start of the range
   * @param endDate end of the range
   * @param token the instrument trading symbol
   * @return historical data containing 30-min candles
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  T get30MinutesOhlc(Date startDate, Date endDate, String token) throws IOException, KiteException;

  /**
   * Retrieves 3-minute OHLC candles for the given symbol and date range.
   *
   * @param startDate start of the range
   * @param endDate end of the range
   * @param token the instrument trading symbol
   * @return historical data containing 3-min candles
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  T get3MinutesOhlc(Date startDate, Date endDate, String token) throws IOException, KiteException;

  /**
   * Returns the current spot price of NIFTY 50.
   *
   * @return the last traded price of NIFTY 50
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  double getNiftySpotPrice() throws IOException, KiteException;

  /**
   * Returns the current premium (last price) of the NIFTY 50 spot.
   *
   * @return the last traded price
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  double getNiftySpotPricePremium() throws IOException, KiteException;

  /**
   * Returns the current premium (last price) for an options contract.
   *
   * @param optionTradingSymbol the trading symbol of the option (e.g. "NIFTY25JUN12345PE")
   * @return the last traded premium
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  double getOptionPremium(String optionTradingSymbol) throws IOException, KiteException;

  /**
   * Places a market sell order for the given symbol and quantity.
   *
   * @param symbol the trading symbol
   * @param quantity the number of lots/units to sell
   * @return the order ID from the exchange
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  String placeSellOrder(String symbol, int quantity) throws IOException, KiteException;

  /**
   * Places a market buy order for the given symbol and quantity.
   *
   * @param symbol the trading symbol
   * @param quantity the number of lots/units to buy
   * @return the order ID from the exchange
   * @throws IOException if a network error occurs
   * @throws KiteException if the Kite API returns an error
   */
  String placeBuyOrder(String symbol, int quantity) throws IOException, KiteException;
}
