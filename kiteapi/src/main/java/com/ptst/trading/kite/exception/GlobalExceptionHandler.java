package com.ptst.trading.kite.exception;

import com.ptst.trading.kite.dto.ApiResponse;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that converts exceptions thrown by controllers into consistent {@link
 * ApiResponse} error responses.
 *
 * <p>Handles Kite API, I/O, illegal state, and generic exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles exceptions thrown by the Kite Connect API client.
   *
   * @param e the Kite exception
   * @return a 500 response with the Kite API error message
   */
  @ExceptionHandler(KiteException.class)
  public ResponseEntity<ApiResponse<Void>> handleKiteException(KiteException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("Kite API Error: " + e.message));
  }

  /**
   * Handles I/O errors during network calls.
   *
   * @param e the IO exception
   * @return a 500 response with the IO error message
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity<ApiResponse<Void>> handleIOException(IOException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("IO Error: " + e.getMessage()));
  }

  /**
   * Handles illegal-state conditions such as missing access tokens.
   *
   * @param e the illegal state exception
   * @return a 400 response with the error message
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

  /**
   * Catch-all handler for any unhandled exception types.
   *
   * @param e the exception
   * @return a 500 response with a generic error message
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
  }
}
