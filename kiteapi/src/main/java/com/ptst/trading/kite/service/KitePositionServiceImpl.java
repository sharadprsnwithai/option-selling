package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link KitePositionService} that delegates to the Kite Connect API via {@link
 * KiteConnect#getPositions()}.
 *
 * <p>Ensures the session is authenticated before each API call.
 */
@Service
public class KitePositionServiceImpl implements KitePositionService {

  private final KiteConnect kiteConnect;
  private final KiteSessionManager sessionManager;

  /**
   * Constructs the service with the Kite API client and session manager.
   *
   * @param kiteConnect the Kite Connect API client
   * @param sessionManager the session manager for authentication
   */
  @Autowired
  public KitePositionServiceImpl(KiteConnect kiteConnect, KiteSessionManager sessionManager) {
    this.kiteConnect = kiteConnect;
    this.sessionManager = sessionManager;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Authenticates the session, then fetches and returns net positions.
   */
  @Override
  public List<Position> getNetPositions() throws KiteException, IOException {
    sessionManager.authenticate();
    Map<String, List<Position>> positions = kiteConnect.getPositions();
    return positions.get("net");
  }
}
