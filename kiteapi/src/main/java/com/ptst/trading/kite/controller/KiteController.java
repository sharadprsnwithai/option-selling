package com.ptst.trading.kite.controller;

import com.ptst.trading.kite.dto.ApiResponse;
import com.ptst.trading.kite.service.KitePositionService;
import com.ptst.trading.kite.service.KiteSessionManager;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing Kite trading API endpoints.
 *
 * <p>Provides endpoints for OAuth login flow, access token management, and retrieving net positions
 * from the Zerodha Kite account.
 */
@RestController
@RequestMapping("/api/kite")
public class KiteController {

  private final KitePositionService positionService;
  private final KiteSessionManager sessionManager;

  /**
   * Constructs the controller with required service dependencies.
   *
   * @param positionService service for retrieving trading positions
   * @param sessionManager service for managing Kite OAuth sessions
   */
  @Autowired
  public KiteController(KitePositionService positionService, KiteSessionManager sessionManager) {
    this.positionService = positionService;
    this.sessionManager = sessionManager;
  }

  /**
   * Returns the Kite Connect login URL. Open this URL in a browser to authenticate with Zerodha and
   * obtain a {@code request_token}.
   *
   * @return response containing the login URL as data
   */
  @GetMapping("/login")
  public ResponseEntity<ApiResponse<String>> getLoginUrl() {
    return ResponseEntity.ok(ApiResponse.success(sessionManager.getLoginURL()));
  }

  /**
   * Retrieves net positions from the Kite trading account. Requires a valid access token to be set
   * beforehand.
   *
   * @return response containing the list of net positions
   * @throws KiteException if the Kite API returns an error
   * @throws IOException if a network error occurs
   */
  @GetMapping("/positions")
  public ResponseEntity<ApiResponse<List<Position>>> getPositions()
      throws KiteException, IOException {
    List<Position> positions = positionService.getNetPositions();
    return ResponseEntity.ok(ApiResponse.success(positions));
  }

  /**
   * Manually sets an access token (alternative to the OAuth callback flow).
   *
   * @param token the access token to set (plain text in request body)
   * @return response confirming the token was updated
   */
  @PostMapping("/access-token")
  public ResponseEntity<ApiResponse<String>> setAccessToken(@RequestBody String token) {
    sessionManager.setAccessToken(token);
    return ResponseEntity.ok(ApiResponse.success("Access token updated successfully", null));
  }

  /**
   * OAuth callback endpoint. Kite redirects here after a successful login, providing a {@code
   * request_token} that is exchanged for an access token via {@link
   * KiteSessionManager#handleCallback(String)}.
   *
   * @param requestToken the temporary request token from Kite
   * @return response containing the permanent access token on success, or an error message on
   *     failure
   */
  @GetMapping("/callback")
  public ResponseEntity<ApiResponse<String>> callback(
      @RequestParam("request_token") String requestToken) {
    try {
      String accessToken = sessionManager.handleCallback(requestToken);
      return ResponseEntity.ok(ApiResponse.success("Authentication successful", accessToken));
    } catch (KiteException e) {
      return ResponseEntity.badRequest().body(ApiResponse.error("Kite API error: " + e.message));
    } catch (IOException e) {
      return ResponseEntity.internalServerError()
          .body(ApiResponse.error("IO error: " + e.getMessage()));
    }
  }
}
