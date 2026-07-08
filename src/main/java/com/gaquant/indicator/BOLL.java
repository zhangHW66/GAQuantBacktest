package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** BOLL 布林带 */
public class BOLL {
    public record BollResult(List<Double> mid, List<Double> upper, List<Double> lower) {}

    public static BollResult calculate(List<Double> closes, int period, double multiplier) {
        return new BollResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
