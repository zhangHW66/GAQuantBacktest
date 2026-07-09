package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** ATR: True Range = max(H-L, |H-C_prev|, |L-C_prev|), EMA平滑 */
public class ATR {
    public static List<Double> calculate(List<Double> highs, List<Double> lows, List<Double> closes,
                                         int period) {
        int n = closes.size();
        List<Double> result = new ArrayList<>(n);
        List<Double> tr = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                tr.add(highs.get(i) - lows.get(i));
            } else {
                double a = highs.get(i) - lows.get(i);
                double b = Math.abs(highs.get(i) - closes.get(i - 1));
                double c = Math.abs(lows.get(i) - closes.get(i - 1));
                tr.add(Math.max(a, Math.max(b, c)));
            }
        }

        // EMA of TR
        double k = 2.0 / (period + 1);
        double atr = tr.get(0);
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                result.add(Double.NaN);
                continue;
            }
            atr = atr + k * (tr.get(i) - atr);
            if (i < period) {
                result.add(Double.NaN);
            } else {
                result.add(atr);
            }
        }
        return result;
    }
}
