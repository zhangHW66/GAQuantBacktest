package com.gaquant.utils;

import java.util.List;

/** 数学工具类 */
public class MathUtil {

    public static double mean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public static double std(List<Double> values) {
        double avg = mean(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    public static double max(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public static double min(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    public static double sum(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }
}
