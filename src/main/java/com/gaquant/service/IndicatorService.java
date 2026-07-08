package com.gaquant.service;

import com.gaquant.model.StockData;
import java.util.List;
import java.util.Map;

/**
 * 技术指标计算服务
 * 指标候选池: MA, MACD, RSI, KDJ, BOLL, ATR, CCI, OBV, Momentum
 */
public class IndicatorService {
    public Map<String, List<Double>> calculateAll(List<StockData> data, Map<String, double[]> params) {
        return Map.of();
    }
}
