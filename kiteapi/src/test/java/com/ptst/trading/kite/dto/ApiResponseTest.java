package com.ptst.trading.kite.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

  @Test
  void successWithData_shouldSetSuccessTrue() {
    ApiResponse<String> response = ApiResponse.success("test-data");

    assertTrue(response.isSuccess());
    assertEquals("Success", response.getMessage());
    assertEquals("test-data", response.getData());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void successWithMessageAndData_shouldUseCustomMessage() {
    ApiResponse<Integer> response = ApiResponse.success("Custom message", 42);

    assertTrue(response.isSuccess());
    assertEquals("Custom message", response.getMessage());
    assertEquals(42, response.getData());
  }

  @Test
  void successWithNullData_shouldAllowNull() {
    ApiResponse<String> response = ApiResponse.success("Done", null);

    assertTrue(response.isSuccess());
    assertEquals("Done", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void error_shouldSetSuccessFalse() {
    ApiResponse<Void> response = ApiResponse.error("Something went wrong");

    assertFalse(response.isSuccess());
    assertEquals("Something went wrong", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void timestamp_shouldBeSetOnCreation() {
    ApiResponse<String> response1 = ApiResponse.success("a");
    ApiResponse<String> response2 = ApiResponse.error("b");

    assertNotNull(response1.getTimestamp());
    assertNotNull(response2.getTimestamp());
  }

  @Test
  void setterAndGetter_shouldWorkCorrectly() {
    ApiResponse<String> response = new ApiResponse<>();

    response.setSuccess(true);
    response.setMessage("msg");
    response.setData("d");

    assertTrue(response.isSuccess());
    assertEquals("msg", response.getMessage());
    assertEquals("d", response.getData());
  }
}
