package com.ptst.trading.kite.controller;

import com.ptst.trading.kite.dto.ApiResponse;
import com.ptst.trading.kite.service.KitePositionService;
import com.ptst.trading.kite.service.KiteSessionManager;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/kite")
public class KiteController {

    private final KitePositionService positionService;
    private final KiteSessionManager sessionManager;

    @Autowired
    public KiteController(KitePositionService positionService, KiteSessionManager sessionManager) {
        this.positionService = positionService;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<String>> getLoginUrl() {
        return ResponseEntity.ok(ApiResponse.success(sessionManager.getLoginURL()));
    }

    @GetMapping("/positions")
    public ResponseEntity<ApiResponse<List<Position>>> getPositions() throws KiteException, IOException {
        List<Position> positions = positionService.getNetPositions();
        return ResponseEntity.ok(ApiResponse.success(positions));
    }

    @PostMapping("/access-token")
    public ResponseEntity<ApiResponse<String>> setAccessToken(@RequestBody String token) {
        sessionManager.setAccessToken(token);
        return ResponseEntity.ok(ApiResponse.success("Access token updated successfully", null));
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<String>> callback(@RequestParam("request_token") String requestToken) {
        try {
            String accessToken = sessionManager.handleCallback(requestToken);
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", accessToken));
        } catch (KiteException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Kite API error: " + e.message));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("IO error: " + e.getMessage()));
        }
    }
}
