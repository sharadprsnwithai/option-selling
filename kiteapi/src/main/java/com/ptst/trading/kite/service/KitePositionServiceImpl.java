package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class KitePositionServiceImpl implements KitePositionService {

    private final KiteConnect kiteConnect;
    private final KiteSessionManager sessionManager;

    @Autowired
    public KitePositionServiceImpl(KiteConnect kiteConnect, KiteSessionManager sessionManager) {
        this.kiteConnect = kiteConnect;
        this.sessionManager = sessionManager;
    }

    @Override
    public List<Position> getNetPositions() throws KiteException, IOException {
        sessionManager.authenticate();
        Map<String, List<Position>> positions = kiteConnect.getPositions();
        return positions.get("net");
    }
}
