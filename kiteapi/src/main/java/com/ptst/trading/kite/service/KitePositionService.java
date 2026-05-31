package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import java.io.IOException;
import java.util.List;

/** Service interface for retrieving trading positions from the Kite account. */
public interface KitePositionService {

  /**
   * Returns the net (consolidated) positions across all instruments.
   *
   * @return list of net positions
   * @throws KiteException if the Kite API returns an error
   * @throws IOException if a network error occurs
   */
  List<Position> getNetPositions() throws KiteException, IOException;
}
