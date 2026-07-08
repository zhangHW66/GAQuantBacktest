package com.gaquant.service;

import com.gaquant.model.TradeRecord;
import java.util.List;
import java.util.Map;

/**
 * 回测服务
 */
public class BacktestService {
    public List<TradeRecord> runBacktest(List<String> signals, double initCapital, double commission) {
        return List.of();
    }

    public Map<String, Double> calculateMetrics(List<TradeRecord> trades) {
        return Map.of();
    }
}
