package com.ptst.trading.kite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import java.io.IOException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KiteSessionManagerImplTest {

  @Mock private KiteConnect kiteConnect;

  private KiteSessionManagerImpl sessionManager;

  @BeforeEach
  void setUp() {
    sessionManager =
        new KiteSessionManagerImpl(
            kiteConnect, "api-secret", "http://localhost:8080/api/kite/callback", null);
  }

  @Test
  void constructor_withValidInitialToken_shouldSetToken() {
    KiteSessionManagerImpl sm =
        new KiteSessionManagerImpl(kiteConnect, "secret", "url", "initial-token");

    assertEquals("initial-token", sm.getAccessToken());
    verify(kiteConnect).setAccessToken("initial-token");
  }

  @Test
  void constructor_withNullInitialToken_shouldNotSetToken() {
    assertNull(sessionManager.getAccessToken());
    verify(kiteConnect, never()).setAccessToken(any());
  }

  @Test
  void constructor_withEmptyInitialToken_shouldNotSetToken() {
    KiteSessionManagerImpl sm = new KiteSessionManagerImpl(kiteConnect, "secret", "url", "");

    assertNull(sm.getAccessToken());
    verify(kiteConnect, never()).setAccessToken(any());
  }

  @Test
  void getLoginURL_shouldDelegateToKiteConnect() {
    when(kiteConnect.getLoginURL())
        .thenReturn("https://kite.zerodha.com/connect/login?api_key=test&v=3");

    String url = sessionManager.getLoginURL();

    assertEquals("https://kite.zerodha.com/connect/login?api_key=test&v=3", url);
    verify(kiteConnect).getLoginURL();
  }

  @Test
  void setAccessToken_shouldUpdateTokenAndKiteConnect() {
    sessionManager.setAccessToken("new-token");

    assertEquals("new-token", sessionManager.getAccessToken());
    verify(kiteConnect).setAccessToken("new-token");
  }

  @Test
  void getAccessToken_initial_shouldReturnNull() {
    assertNull(sessionManager.getAccessToken());
  }

  @Test
  void authenticate_withToken_shouldSetOnKiteConnect() {
    sessionManager.setAccessToken("valid-token");

    sessionManager.authenticate();

    verify(kiteConnect, times(2)).setAccessToken("valid-token");
  }

  @Test
  void authenticate_withoutToken_shouldThrow() {
    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> sessionManager.authenticate());
    assertEquals("Access token is not set. Please authenticate first.", ex.getMessage());
  }

  @Test
  void handleCallback_shouldExchangeTokenAndReturnAccessToken()
      throws KiteException, IOException, JSONException {
    User mockUser = new User();
    mockUser.accessToken = "session-access-token";
    when(kiteConnect.generateSession("request-token", "api-secret")).thenReturn(mockUser);

    String result = sessionManager.handleCallback("request-token");

    assertEquals("session-access-token", result);
    assertEquals("session-access-token", sessionManager.getAccessToken());
    verify(kiteConnect).setAccessToken("session-access-token");
  }

  @Test
  void handleCallback_whenKiteException_shouldPropagate()
      throws KiteException, IOException, JSONException {
    when(kiteConnect.generateSession("bad-token", "api-secret"))
        .thenThrow(new KiteException("Invalid token", 400));

    KiteException ex =
        assertThrows(KiteException.class, () -> sessionManager.handleCallback("bad-token"));
    assertEquals("Invalid token", ex.message);
  }
}
