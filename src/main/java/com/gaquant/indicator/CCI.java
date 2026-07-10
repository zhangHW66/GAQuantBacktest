package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** CCI: TP=(H+L+C)/3, MA(TP), 平均偏差, CCI=(TP-MA)/(0.015×MD) */
public class CCI {
    public static List<Double> calculate(List<Double> highs, List<Double> lows, List<Double> closes,
                                         int period) {
        int n = closes.size();
        List<Double> result = new ArrayList<>(n);

        double[] tp = new double[n];
        for (int i = 0; i < n; i++)
            tp[i] = (highs.get(i) + lows.get(i) + closes.get(i)) / 3.0;

        for (int i = 0; i < n; i++) {
            if (i < period - 1) {
                result.add(Double.NaN);
                continue;
            }
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += tp[j];
            double ma = sum / period;

            double md = 0;
            for (int j = i - period + 1; j <= i; j++) md += Math.abs(tp[j] - ma);
            md /= period;

            result.add(md == 0 ? 0 : (tp[i] - ma) / (0.015 * md));
        }
        return result;
    }
}
