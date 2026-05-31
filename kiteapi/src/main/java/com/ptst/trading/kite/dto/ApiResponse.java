package com.ptst.trading.kite.dto;

import java.time.Instant;

/**
 * Generic API response wrapper used consistently across all REST endpoints.
 *
 * <p>Encapsulates success status, a human-readable message, the response payload, and a UTC
 * timestamp of when the response was created.
 *
 * @param <T> the type of the data payload
 */
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private Instant timestamp;

  /** Creates a new response with the current UTC timestamp. */
  public ApiResponse() {
    this.timestamp = Instant.now();
  }

  /**
   * Creates a fully populated response.
   *
   * @param success whether the operation succeeded
   * @param message a human-readable status message
   * @param data the response payload
   */
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = Instant.now();
  }

  /**
   * Creates a success response with the default message "Success".
   *
   * @param data the response payload
   * @param <T> the type of the payload
   * @return a success response
   */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "Success", data);
  }

  /**
   * Creates a success response with a custom message.
   *
   * @param message a custom success message
   * @param data the response payload
   * @param <T> the type of the payload
   * @return a success response
   */
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  /**
   * Creates an error response with the given message and {@code null} data.
   *
   * @param message a description of the error
   * @param <T> the type of the payload
   * @return an error response
   */
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  /**
   * @return {@code true} if the operation succeeded
   */
  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * @return a human-readable status message
   */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the response payload
   */
  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  /**
   * @return the UTC timestamp when this response was created
   */
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }
}
