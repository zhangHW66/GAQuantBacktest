package com.gaquant.service;

import com.gaquant.indicator.*;
import com.gaquant.model.StockData;
import java.util.*;

/**
 * 技术指标分发服务
 * 根据参数Map调用对应指标类，返回 指标名→数值列表
 * 多输出指标取代表值: MACD→柱, KDJ→K, BOLL→中轨
 */
public class IndicatorService {

    public Map<String, List<Double>> calculateAll(List<StockData> data, Map<String, double[]> params) {
        if (data.isEmpty() || params == null || params.isEmpty()) return Map.of();

        int n = data.size();
        List<Double> closes = new ArrayList<>(n);
        List<Double> highs  = new ArrayList<>(n);
        List<Double> lows   = new ArrayList<>(n);
        List<Long>   volumes = new ArrayList<>(n);
        for (StockData sd : data) {
            closes.add(sd.getClose());
            highs.add(sd.getHigh());
            lows.add(sd.getLow());
            volumes.add(sd.getVolume());
        }

        Map<String, List<Double>> results = new LinkedHashMap<>();

        for (String name : params.keySet()) {
            double[] p = params.get(name);
            if (p == null || p.length == 0) continue;

            try {
                switch (name) {
                    case "MA" -> results.put("MA", MA.calculate(closes, (int) p[0]));
                    case "MACD" -> {
                        int fast = (int) p[0], slow = (int) p[1], sig = (int) p[2];
                        results.put("MACD", MACD.calculate(closes, fast, slow, sig).bar());
                    }
                    case "RSI" -> results.put("RSI", RSI.calculate(closes, (int) p[0]));
                    case "KDJ" -> results.put("KDJ",
                        KDJ.calculate(highs, lows, closes, (int) p[0]).k());
                    case "BOLL" -> results.put("BOLL",
                        BOLL.calculate(closes, (int) p[0], p[1]).mid());
                    case "ATR" -> results.put("ATR", ATR.calculate(highs, lows, closes, (int) p[0]));
                    case "CCI" -> results.put("CCI", CCI.calculate(highs, lows, closes, (int) p[0]));
                    case "OBV" -> results.put("OBV", OBV.calculate(closes, volumes));
                    case "Momentum" -> results.put("Momentum",
                        Momentum.calculate(closes, (int) p[0]));
                }
            } catch (Exception e) {
                System.err.println("Indicator " + name + " failed: " + e.getMessage());
            }
        }
        return results;
    }
}
