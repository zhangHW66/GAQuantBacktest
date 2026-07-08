package com.gaquant.config;

import java.util.*;

/**
 * 指标参数候选池
 * 每类指标预置多组差异化参数，形成规模化候选池供GA筛选
 * 非固定使用单一参数
 */
public class IndicatorConfig {

    public static final Map<String, List<double[]>> CANDIDATES = new LinkedHashMap<>();

    static {
        // 趋势类
        CANDIDATES.put("MA", List.of(
            new double[]{5}, new double[]{10}, new double[]{20}, new double[]{60}, new double[]{120}
        ));
        CANDIDATES.put("MACD", List.of(
            new double[]{12, 26, 9}, new double[]{10, 20, 7}, new double[]{8, 17, 9}
        ));

        // 震荡类
        CANDIDATES.put("RSI", List.of(
            new double[]{6}, new double[]{12}, new double[]{24}
        ));
        CANDIDATES.put("KDJ", List.of(
            new double[]{9}, new double[]{14}, new double[]{21}
        ));
        CANDIDATES.put("BOLL", List.of(
            new double[]{20, 2.0}, new double[]{26, 2.0}, new double[]{20, 2.5}
        ));

        // 波动率类
        CANDIDATES.put("ATR", List.of(
            new double[]{7}, new double[]{14}, new double[]{21}
        ));

        // 成交量类
        CANDIDATES.put("OBV", List.of(
            new double[]{5}, new double[]{10}, new double[]{20}
        ));

        // 其他
        CANDIDATES.put("CCI", List.of(
            new double[]{14}, new double[]{20}, new double[]{28}
        ));
        CANDIDATES.put("Momentum", List.of(
            new double[]{5}, new double[]{10}, new double[]{20}, new double[]{60}
        ));
    }

    /** 从候选池随机选一组参数 */
    public static double[] randomParam(String name) {
        List<double[]> c = CANDIDATES.get(name);
        return (c == null || c.isEmpty()) ? new double[0]
            : c.get(new Random().nextInt(c.size()));
    }

    /** 某指标候选参数组数量 */
    public static int candidateCount(String name) {
        List<double[]> c = CANDIDATES.get(name);
        return c == null ? 0 : c.size();
    }
}
