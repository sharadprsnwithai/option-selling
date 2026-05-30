package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import java.io.IOException;

public interface KiteSessionManager {
    String getLoginURL();
    void setAccessToken(String token);
    String getAccessToken();
    void authenticate();
    String handleCallback(String requestToken) throws KiteException, IOException;
}
