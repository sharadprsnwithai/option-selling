package com.ptst.trading.kite.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that creates the {@link KiteConnect} bean used throughout the application to
 * communicate with the Zerodha Kite API.
 *
 * <p>API key and user ID are injected from external configuration properties.
 */
@Configuration
public class KiteConfig {

  @Value("${kite.api.key}")
  private String apiKey;

  @Value("${kite.user.id}")
  private String userId;

  /**
   * Creates the singleton {@link KiteConnect} bean configured with the application's API key and
   * user ID.
   *
   * @return a configured {@link KiteConnect} instance
   */
  @Bean
  public KiteConnect kiteConnect() {
    KiteConnect kiteConnect = new KiteConnect(apiKey);
    kiteConnect.setUserId(userId);
    return kiteConnect;
  }
}
