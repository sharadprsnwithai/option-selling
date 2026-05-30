package com.ptst.trading.kite.exception;

import com.ptst.trading.kite.dto.ApiResponse;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleKiteException_shouldReturn500() {
        KiteException ex = new KiteException("Token expired", 401);

        ResponseEntity<ApiResponse<Void>> response = handler.handleKiteException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Kite API Error: Token expired", body.getMessage());
    }

    @Test
    void handleIOException_shouldReturn500() {
        IOException ex = new IOException("Connection refused");

        ResponseEntity<ApiResponse<Void>> response = handler.handleIOException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("IO Error: Connection refused", body.getMessage());
    }

    @Test
    void handleIllegalStateException_shouldReturn400() {
        IllegalStateException ex = new IllegalStateException("Token not set");

        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Token not set", body.getMessage());
    }

    @Test
    void handleGeneralException_shouldReturn500() {
        RuntimeException ex = new RuntimeException("Unexpected failure");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("An unexpected error occurred: Unexpected failure", body.getMessage());
    }
}
