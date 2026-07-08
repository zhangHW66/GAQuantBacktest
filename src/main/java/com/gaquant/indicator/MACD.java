package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** MACD 指数平滑异同移动平均 */
public class MACD {
    public record MacdResult(List<Double> dif, List<Double> dea, List<Double> histogram) {}

    public static MacdResult calculate(List<Double> closes, int fast, int slow, int signal) {
        return new MacdResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
