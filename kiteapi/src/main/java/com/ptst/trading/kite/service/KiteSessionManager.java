package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import java.io.IOException;

/**
 * Manages the lifecycle of a Kite Connect OAuth session, including login URL generation, access
 * token storage, and session authentication.
 */
public interface KiteSessionManager {

  /**
   * Returns the Kite Connect login URL for initiating the OAuth flow.
   *
   * @return the login URL
   */
  String getLoginURL();

  /**
   * Stores an access token and configures it on the Kite API client.
   *
   * @param token the access token
   */
  void setAccessToken(String token);

  /**
   * Returns the currently stored access token, or {@code null} if none is set.
   *
   * @return the access token, or {@code null}
   */
  String getAccessToken();

  /**
   * Ensures the session is authenticated. Throws an exception if no access token is available.
   *
   * @throws IllegalStateException if the access token is not set
   */
  void authenticate();

  /**
   * Exchanges a temporary request token for a permanent access token by calling {@code
   * generateSession} on the Kite API.
   *
   * @param requestToken the temporary token received from the OAuth callback
   * @return the permanent access token
   * @throws KiteException if the Kite API returns an error
   * @throws IOException if a network error occurs
   */
  String handleCallback(String requestToken) throws KiteException, IOException;
}
