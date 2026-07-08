package com.gaquant.service;

import com.gaquant.model.Chromosome;
import com.gaquant.model.StockData;
import java.util.List;

/**
 * 滚动窗口预测服务
 */
public class RollingService {
    public List<String> predict(List<StockData> data, Chromosome strategy, int trainMonths, int testMonths) {
        return List.of();
    }
}
