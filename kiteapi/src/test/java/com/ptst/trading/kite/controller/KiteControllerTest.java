package com.ptst.trading.kite.controller;

import com.ptst.trading.kite.service.KitePositionService;
import com.ptst.trading.kite.service.KiteSessionManager;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KiteController.class)
public class KiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KitePositionService positionService;

    @MockitoBean
    private KiteSessionManager sessionManager;

    @Test
    public void testGetPositions() throws KiteException, IOException, Exception {
        doReturn(Collections.emptyList()).when(positionService).getNetPositions();

        mockMvc.perform(get("/api/kite/positions"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetLoginUrl() throws Exception {
        when(sessionManager.getLoginURL()).thenReturn("http://mock-login-url");

        mockMvc.perform(get("/api/kite/login"))
                .andExpect(status().isOk());
    }
}
