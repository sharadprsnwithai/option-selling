package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KitePositionServiceImplTest {

    @Mock
    private KiteConnect kiteConnect;

    @Mock
    private KiteSessionManager sessionManager;

    private KitePositionServiceImpl positionService;

    @BeforeEach
    void setUp() {
        positionService = new KitePositionServiceImpl(kiteConnect, sessionManager);
    }

    @Test
    void getNetPositions_shouldReturnNetList() throws KiteException, IOException {
        Position p1 = new Position();
        Position p2 = new Position();
        Map<String, List<Position>> positions = new HashMap<>();
        positions.put("net", Arrays.asList(p1, p2));
        positions.put("day", Arrays.asList());
        when(kiteConnect.getPositions()).thenReturn(positions);

        List<Position> result = positionService.getNetPositions();

        assertEquals(2, result.size());
        verify(sessionManager).authenticate();
    }

    @Test
    void getNetPositions_whenEmpty_shouldReturnEmptyList() throws KiteException, IOException {
        Map<String, List<Position>> positions = new HashMap<>();
        positions.put("net", Arrays.asList());
        positions.put("day", Arrays.asList());
        when(kiteConnect.getPositions()).thenReturn(positions);

        List<Position> result = positionService.getNetPositions();

        assertTrue(result.isEmpty());
    }

    @Test
    void getNetPositions_whenKiteException_shouldPropagate() throws KiteException, IOException {
        when(kiteConnect.getPositions()).thenThrow(new KiteException("API error", 500));

        KiteException ex = assertThrows(KiteException.class, () -> positionService.getNetPositions());
        assertEquals("API error", ex.message);
    }

    @Test
    void getNetPositions_shouldCallAuthenticateBeforeFetch() throws KiteException, IOException {
        Map<String, List<Position>> positions = new HashMap<>();
        positions.put("net", Arrays.asList());
        when(kiteConnect.getPositions()).thenReturn(positions);

        positionService.getNetPositions();

        verify(sessionManager).authenticate();
        verify(kiteConnect).getPositions();
    }
}
