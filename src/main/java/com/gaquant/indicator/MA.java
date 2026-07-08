package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** 移动平均线计算 */
public class MA {
    public static List<Double> calculate(List<Double> closes, int period) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < closes.size(); i++) {
            if (i < period - 1) { result.add(Double.NaN); continue; }
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += closes.get(j);
            result.add(sum / period);
        }
        return result;
    }
}
