package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** KDJ 随机指标 */
public class KDJ {
    public record KdjResult(List<Double> k, List<Double> d, List<Double> j) {}

    public static KdjResult calculate(List<Double> highs, List<Double> lows, List<Double> closes,
                                       int period) {
        return new KdjResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
