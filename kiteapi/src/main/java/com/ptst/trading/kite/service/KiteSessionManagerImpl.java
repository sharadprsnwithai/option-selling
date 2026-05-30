package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KiteSessionManagerImpl implements KiteSessionManager {

    private final KiteConnect kiteConnect;
    private final String apiSecret;
    private final String redirectUrl;
    private String accessToken;

    @Autowired
    public KiteSessionManagerImpl(KiteConnect kiteConnect,
                                  @Value("${kite.api.secret}") String apiSecret,
                                  @Value("${kite.redirect.url}") String redirectUrl,
                                  @Value("${kite.access.token:#{null}}") String initialToken) {
        this.kiteConnect = kiteConnect;
        this.apiSecret = apiSecret;
        this.redirectUrl = redirectUrl;
        this.accessToken = initialToken;
        if (this.accessToken != null && !this.accessToken.isEmpty()) {
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

    @Override
    public void authenticate() {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token is not set. Please authenticate first.");
        }
        kiteConnect.setAccessToken(accessToken);
    }

    @Override
    public String handleCallback(String requestToken) throws KiteException, IOException {
        User user = kiteConnect.generateSession(requestToken, apiSecret);
        this.accessToken = user.accessToken;
        kiteConnect.setAccessToken(this.accessToken);
        return this.accessToken;
    }
}
