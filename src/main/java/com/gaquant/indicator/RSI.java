package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** RSI: Wilder平滑法, avgGain/avgLoss → RS → 100-100/(1+RS) */
public class RSI {
    public static List<Double> calculate(List<Double> closes, int period) {
        int n = closes.size();
        List<Double> result = new ArrayList<>(n);

        double avgGain = 0, avgLoss = 0;

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                result.add(Double.NaN);
                continue;
            }
            double change = closes.get(i) - closes.get(i - 1);
            double gain = Math.max(0, change);
            double loss = Math.max(0, -change);

            if (i < period) {
                avgGain += gain;
                avgLoss += loss;
                if (i == period) { avgGain /= period; avgLoss /= period; }
            } else if (i == period) {
                avgGain = (avgGain + gain) / period;
                avgLoss = (avgLoss + loss) / period;
            } else {
                avgGain = (avgGain * (period - 1) + gain) / period;
                avgLoss = (avgLoss * (period - 1) + loss) / period;
            }

            if (i < period) {
                result.add(Double.NaN);
            } else {
                double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
                result.add(100 - 100 / (1 + rs));
            }
        }
        return result;
    }
}
