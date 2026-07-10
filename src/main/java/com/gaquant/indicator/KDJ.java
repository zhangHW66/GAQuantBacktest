package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** KDJ: RSV→K→D→J, K=2/3*K_prev+1/3*RSV, D=2/3*D_prev+1/3*K, J=3K-2D */
public class KDJ {
    public record KdjResult(List<Double> k, List<Double> d, List<Double> j) {}

    public static KdjResult calculate(List<Double> highs, List<Double> lows, List<Double> closes,
                                       int period) {
        int n = closes.size();
        List<Double> k = new ArrayList<>(n);
        List<Double> d = new ArrayList<>(n);
        List<Double> j = new ArrayList<>(n);

        double prevK = 50, prevD = 50;

        for (int i = 0; i < n; i++) {
            if (i < period - 1) {
                k.add(Double.NaN); d.add(Double.NaN); j.add(Double.NaN);
                continue;
            }

            double lo = lows.get(i), hi = highs.get(i);
            for (int t = i - period + 1; t <= i; t++) {
                if (lows.get(t) < lo) lo = lows.get(t);
                if (highs.get(t) > hi) hi = highs.get(t);
            }
            double rsv = (hi == lo) ? 50 : (closes.get(i) - lo) / (hi - lo) * 100;

            double curK = (i == period - 1)
                ? (2.0 / 3 * prevK + 1.0 / 3 * rsv)
                : (2.0 / 3 * prevK + 1.0 / 3 * rsv);
            double curD = (i == period - 1)
                ? (2.0 / 3 * prevD + 1.0 / 3 * curK)
                : (2.0 / 3 * prevD + 1.0 / 3 * curK);

            k.add(curK);
            d.add(curD);
            j.add(3 * curK - 2 * curD);
            prevK = curK;
            prevD = curD;
        }
        return new KdjResult(k, d, j);
    }
}
