package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** BOLL: 中轨=MA(period), 上/下轨=中轨 ± multiplier×标准差 */
public class BOLL {
    public record BollResult(List<Double> mid, List<Double> upper, List<Double> lower) {}

    public static BollResult calculate(List<Double> closes, int period, double multiplier) {
        int n = closes.size();
        List<Double> mid = new ArrayList<>(n);
        List<Double> upper = new ArrayList<>(n);
        List<Double> lower = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (i < period - 1) {
                mid.add(Double.NaN); upper.add(Double.NaN); lower.add(Double.NaN);
                continue;
            }
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += closes.get(j);
            double ma = sum / period;

            double variance = 0;
            for (int j = i - period + 1; j <= i; j++)
                variance += Math.pow(closes.get(j) - ma, 2);
            double std = Math.sqrt(variance / period);

            mid.add(ma);
            upper.add(ma + multiplier * std);
            lower.add(ma - multiplier * std);
        }
        return new BollResult(mid, upper, lower);
    }
}
