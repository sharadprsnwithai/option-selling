package com.ptst.trading.kite.service;

import com.ptst.trading.kite.util.AppUtils;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link MarketDataService} that communicates with the Kite Connect REST API to
 * fetch historical OHLC data, real-time quotes, and to place buy/sell orders.
 *
 * <p>Caches instrument-to-token mappings in an in-memory map to avoid repeated API calls for token
 * resolution.
 */
@Component
public class KiteMarketDataServiceImpl implements MarketDataService<HistoricalData> {

  private static final Logger log = LoggerFactory.getLogger(KiteMarketDataServiceImpl.class);

  private final KiteConnect kiteConnect;
  private final Map<String, Long> symbolTokenMap = new HashMap<>();

  /**
   * Constructs the service with the Kite Connect API client.
   *
   * @param kiteConnect the Kite Connect API client
   */
  public KiteMarketDataServiceImpl(KiteConnect kiteConnect) {
    this.kiteConnect = kiteConnect;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Fetches 30-minute candles by resolving the symbol to a token and calling the Kite historical
   * data API.
   */
  @Override
  public HistoricalData get30MinutesOhlc(Date startDate, Date endDate, String symbol)
      throws IOException, KiteException {
    return kiteConnect.getHistoricalData(
        Date.from(startDate.toInstant()),
        Date.from(endDate.toInstant()),
        String.valueOf(getSymbolToken(symbol)),
        "30minute",
        false,
        false);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Fetches 3-minute candles by resolving the symbol to a token and calling the Kite historical
   * data API.
   */
  @Override
  public HistoricalData get3MinutesOhlc(Date startDate, Date endDate, String symbol)
      throws IOException, KiteException {
    return kiteConnect.getHistoricalData(
        Date.from(startDate.toInstant()),
        Date.from(endDate.toInstant()),
        String.valueOf(getSymbolToken(symbol)),
        "3minute",
        false,
        false);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Resolves the NIFTY 50 symbol token and fetches its last traded price.
   */
  @Override
  public double getNiftySpotPrice() throws KiteException, IOException {
    String niftyToken = String.valueOf(getSymbolToken(AppUtils.NIFTY_SYMBOL));
    Map<String, Quote> quotes = kiteConnect.getQuote(new String[] {niftyToken});
    return quotes.get(niftyToken).lastPrice;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Fetches the last traded price of the NIFTY 50 index.
   */
  @Override
  public double getNiftySpotPricePremium() throws IOException, KiteException {
    String niftyToken = String.valueOf(getSymbolToken(AppUtils.NIFTY_SYMBOL));
    Map<String, Quote> quotes = kiteConnect.getQuote(new String[] {niftyToken});
    Quote optionQuote = quotes.get(niftyToken);
    return optionQuote.lastPrice;
  }

  /** {@inheritDoc} */
  @Override
  public double getOptionPremium(String optionTradingSymbol) throws IOException, KiteException {
    String niftyToken = String.valueOf(getSymbolToken(AppUtils.NIFTY_SYMBOL));
    Map<String, Quote> quotes = kiteConnect.getQuote(new String[] {niftyToken});
    Quote optionQuote = quotes.get(niftyToken);
    return optionQuote.lastPrice;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Creates a regular MARKET SELL order on the NFO exchange with NRML product type.
   */
  @Override
  public String placeSellOrder(String symbol, int quantity) throws IOException, KiteException {
    OrderParams params = new OrderParams();
    params.exchange = AppUtils.NFO;
    params.tradingsymbol = symbol;
    params.transactionType = "SELL";
    params.quantity = quantity;
    params.orderType = "MARKET";
    params.product = "NRML";
    OrderResponse response = kiteConnect.placeOrder(params, "regular");
    return response.orderId;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Creates a regular MARKET BUY order on the NFO exchange with NRML product type.
   */
  @Override
  public String placeBuyOrder(String symbol, int quantity) throws IOException, KiteException {
    OrderParams params = new OrderParams();
    params.exchange = AppUtils.NFO;
    params.tradingsymbol = symbol;
    params.transactionType = "BUY";
    params.quantity = quantity;
    params.orderType = "MARKET";
    params.product = "NRML";
    OrderResponse response = kiteConnect.placeOrder(params, "regular");
    return response.orderId;
  }

  /**
   * Resolves a trading symbol to its Kite instrument token.
   *
   * <p>Results are cached in {@link #symbolTokenMap} to minimise API calls.
   *
   * @param symbol the trading symbol (e.g. "NIFTY 50")
   * @return the numeric instrument token, or {@code 0} if not found
   * @throws KiteException if the Kite API returns an error
   * @throws IOException if a network error occurs
   */
  private long getSymbolToken(String symbol) throws KiteException, IOException {
    if (symbolTokenMap.containsKey(symbol)) {
      return symbolTokenMap.get(symbol);
    } else {
      List<Instrument> instruments = kiteConnect.getInstruments(AppUtils.NSE);
      for (Instrument inst : instruments) {
        if (symbol.equalsIgnoreCase(inst.tradingsymbol)) {
          symbolTokenMap.put(symbol, inst.instrument_token);
          return inst.instrument_token;
        }
      }
    }
    log.error("{} instrument not found on NSE", symbol);
    return 0;
  }
}
