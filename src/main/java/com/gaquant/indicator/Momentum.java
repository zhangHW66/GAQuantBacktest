package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** 动量: close / close[N日前] × 100 */
public class Momentum {
    public static List<Double> calculate(List<Double> closes, int period) {
        int n = closes.size();
        List<Double> result = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (i < period) {
                result.add(Double.NaN);
            } else {
                double prev = closes.get(i - period);
                result.add(prev == 0 ? 100 : closes.get(i) / prev * 100);
            }
        }
        return result;
    }
}
