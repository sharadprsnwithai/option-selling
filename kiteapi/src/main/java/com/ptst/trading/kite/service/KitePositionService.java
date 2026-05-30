package com.ptst.trading.kite.service;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Position;

import java.io.IOException;
import java.util.List;

public interface KitePositionService {
    List<Position> getNetPositions() throws KiteException, IOException;
}
