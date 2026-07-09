package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** MACD: DIF=EMA(fast)-EMA(slow), DEA=EMA(DIF,signal), 柱=DIF-DEA */
public class MACD {
    public record MacdResult(List<Double> dif, List<Double> dea, List<Double> bar) {}

    public static MacdResult calculate(List<Double> closes, int fast, int slow, int signal) {
        int n = closes.size();
        double[] emaFast = ema(closes, fast);
        double[] emaSlow = ema(closes, slow);

        double[] dif = new double[n];
        for (int i = 0; i < n; i++) dif[i] = emaFast[i] - emaSlow[i];

        double[] dea = ema(dif, signal);

        List<Double> difList = new ArrayList<>(n);
        List<Double> deaList = new ArrayList<>(n);
        List<Double> barList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            difList.add(dif[i]);
            deaList.add(dea[i]);
            barList.add((dif[i] - dea[i]) * 2);
        }
        return new MacdResult(difList, deaList, barList);
    }

    private static double[] ema(List<Double> vals, int period) {
        int n = vals.size();
        double[] r = new double[n];
        double k = 2.0 / (period + 1);
        r[0] = vals.get(0);
        for (int i = 1; i < n; i++)
            r[i] = r[i - 1] + k * (vals.get(i) - r[i - 1]);
        return r;
    }

    private static double[] ema(double[] vals, int period) {
        int n = vals.length;
        double[] r = new double[n];
        double k = 2.0 / (period + 1);
        r[0] = vals[0];
        for (int i = 1; i < n; i++)
            r[i] = r[i - 1] + k * (vals[i] - r[i - 1]);
        return r;
    }
}
