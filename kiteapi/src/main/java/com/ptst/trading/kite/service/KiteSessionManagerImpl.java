package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link KiteSessionManager} that manages the Kite Connect OAuth session. Stores
 * the access token in memory and configures it on the shared {@link KiteConnect} instance.
 *
 * <p>An initial access token may be injected via the {@code kite.access.token} configuration
 * property, allowing the session to survive restarts.
 */
@Component
public class KiteSessionManagerImpl implements KiteSessionManager {

  private final KiteConnect kiteConnect;
  private final String apiSecret;
  private String accessToken;

  /**
   * Constructs the session manager. If an {@code initialToken} is provided via configuration, it is
   * set on the Kite client immediately.
   *
   * @param kiteConnect the Kite Connect API client
   * @param apiSecret the Kite API secret
   * @param redirectUrl the OAuth redirect URL configured in the Kite console
   * @param initialToken an optional pre-existing access token
   */
  @Autowired
  public KiteSessionManagerImpl(
      KiteConnect kiteConnect,
      @Value("${kite.api.secret}") String apiSecret,
      @Value("${kite.redirect.url}") String redirectUrl,
      @Value("${kite.access.token:#{null}}") String initialToken) {
    this.kiteConnect = kiteConnect;
    this.apiSecret = apiSecret;
    this.accessToken = (initialToken != null && !initialToken.isEmpty()) ? initialToken : null;
    if (this.accessToken != null) {
      this.kiteConnect.setAccessToken(this.accessToken);
    }
  }

  @Override
  public String getLoginURL() {
    return kiteConnect.getLoginURL();
  }

  @Override
  public void setAccessToken(String token) {
    this.accessToken = token;
    this.kiteConnect.setAccessToken(token);
  }

  @Override
  public String getAccessToken() {
    return this.accessToken;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Re-applies the stored token to the Kite client to ensure it is active.
   */
  @Override
  public void authenticate() {
    if (accessToken == null || accessToken.isEmpty()) {
      throw new IllegalStateException("Access token is not set. Please authenticate first.");
    }
    kiteConnect.setAccessToken(accessToken);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calls {@link KiteConnect#generateSession} with the request token and API secret, then stores
   * the returned access token in memory.
   */
  @Override
  public String handleCallback(String requestToken) throws KiteException, IOException {
    User user = kiteConnect.generateSession(requestToken, apiSecret);
    this.accessToken = user.accessToken;
    kiteConnect.setAccessToken(this.accessToken);
    return this.accessToken;
  }
}
