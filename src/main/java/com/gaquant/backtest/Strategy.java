package com.gaquant.backtest;

import com.gaquant.model.Chromosome;
import com.gaquant.model.StockData;
import com.gaquant.service.IndicatorService;
import java.util.*;

/**
 * 交易策略 — 多指标加权融合 + 阈值判定
 * 逐日计算所有选中指标 → 归一化 → 加权融合 → 与阈值比较 → BUY/SELL/HOLD
 */
public class Strategy {

    private final IndicatorService indicatorService = new IndicatorService();

    public List<String> generateSignals(List<StockData> data, Chromosome chromosome) {
        Map<String, Boolean> enabled = chromosome.getIndicatorEnabled();
        Map<String, Double> weights = chromosome.getIndicatorWeights();
        Map<String, double[]> params = chromosome.getIndicatorParams();
        double buyThreshold = chromosome.getBuyThreshold();
        double sellThreshold = chromosome.getSellThreshold();

        // 计算所有指标原始值
        Map<String, List<Double>> rawValues = indicatorService.calculateAll(data, params);

        int n = data.size();
        List<String> signals = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double score = 0;
            double totalW = 0;

            for (String name : enabled.keySet()) {
                if (!enabled.get(name)) continue;
                List<Double> vals = rawValues.get(name);
                if (vals == null || i >= vals.size()) continue;
                double v = vals.get(i);
                if (Double.isNaN(v)) continue;

                double normalized = normalize(name, v, data.get(i));
                double w = weights.getOrDefault(name, 0.0);
                score += normalized * w;
                totalW += Math.abs(w);
            }

            if (totalW > 0) score /= totalW;

            if (score > buyThreshold) signals.add("BUY");
            else if (score < sellThreshold) signals.add("SELL");
            else signals.add("HOLD");
        }
        return signals;
    }

    /** 将各指标原始值归一化到 [-1, 1] 区间 */
    private double normalize(String indicatorName, double rawValue, StockData sd) {
        return switch (indicatorName) {
            case "RSI"  -> (rawValue - 50) / 50;           // 0~100 → [-1,1]
            case "CCI"  -> Math.max(-3, Math.min(3, rawValue / 100)); // clamp
            case "Momentum" -> (rawValue - 100) / 100;
            case "MA"   -> (sd.getClose() - rawValue) / sd.getClose();
            case "BOLL" -> (sd.getClose() - rawValue) / (sd.getClose() * 0.02);
            case "KDJ"  -> (rawValue - 50) / 50;
            default     -> rawValue; // MACD/ATR/OBV 等本身适合直接融合
        };
    }
}
