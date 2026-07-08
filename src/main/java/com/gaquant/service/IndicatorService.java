package com.gaquant.service;

import com.gaquant.model.StockData;
import java.util.List;
import java.util.Map;

/**
 * 技术指标计算服务
 */
public class IndicatorService {
    public Map<String, List<Double>> calculateAll(List<StockData> data, Map<String, int[]> params) {
        return Map.of();
    }
}
