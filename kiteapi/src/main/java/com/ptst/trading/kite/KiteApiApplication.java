package com.ptst.trading.kite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Kite API Option Selling Trading Platform.
 *
 * <p>This Spring Boot application integrates with Zerodha Kite Connect to handle OAuth
 * authentication, session management, position retrieval, market data fetching, and algorithmic
 * option selling strategies.
 *
 * <p>Scheduling is enabled to support cron-based strategy execution.
 */
@SpringBootApplication
@EnableScheduling
public class KiteApiApplication {

  /**
   * Application entry point. Loads environment variables from a {@code .env} file (if present) into
   * system properties before bootstrapping Spring.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().directory("./kiteapi").ignoreIfMissing().load();
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

    SpringApplication.run(KiteApiApplication.class, args);
  }
}
