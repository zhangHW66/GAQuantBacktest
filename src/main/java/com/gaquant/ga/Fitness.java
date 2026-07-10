package com.gaquant.ga;

import com.gaquant.backtest.Strategy;
import com.gaquant.config.AppConfig;
import com.gaquant.model.Chromosome;
import com.gaquant.model.StockData;
import java.util.*;

/**
 * 五维综合适应度
 * 内部做简化版回测：信号 → 交易模拟 → 五维指标 → 加权融合
 */
public class Fitness {

    private static final double RISK_FREE_RATE = 0.02;

    public static double calculate(Chromosome chromosome, List<StockData> data) {
        if (data.size() < 2) return -999;

        Strategy strategy = new Strategy();
        List<String> signals = strategy.generateSignals(data, chromosome);
        AppConfig cfg = AppConfig.getInstance();

        double cash = cfg.getInitCapital();
        double shares = 0;
        double commission = cfg.getCommission();
        double slippage = cfg.getSlippage();

        List<Double> dailyValues = new ArrayList<>(data.size());
        List<Double> tradeReturns = new ArrayList<>();
        double peakValue = cash;
        double maxDrawdown = 0;
        double lastBuyPrice = 0;

        for (int i = 0; i < signals.size(); i++) {
            double close = data.get(i).getClose();
            String sig = signals.get(i);

            if ("BUY".equals(sig) && shares == 0) {
                double cost = close * (1 + slippage);
                double buyCash = cash * (1 - commission);
                shares = buyCash / cost;
                cash = 0;
                lastBuyPrice = cost;
            } else if ("SELL".equals(sig) && shares > 0) {
                double sellPrice = close * (1 - slippage);
                cash = shares * sellPrice * (1 - commission);
                double ret = (cash - lastBuyPrice * shares) / (lastBuyPrice * shares);
                tradeReturns.add(ret);
                shares = 0;
            }

            double value = cash + shares * close;
            dailyValues.add(value);
            if (value > peakValue) peakValue = value;
            double dd = (peakValue - value) / peakValue;
            if (dd > maxDrawdown) maxDrawdown = dd;
        }

        // 期末清仓
        if (shares > 0 && !data.isEmpty()) {
            double finalClose = data.get(data.size() - 1).getClose();
            cash = shares * finalClose * (1 - slippage) * (1 - commission);
            double ret = (cash - lastBuyPrice * shares) / (lastBuyPrice * shares);
            tradeReturns.add(ret);
            dailyValues.set(dailyValues.size() - 1, cash);
        }

        double finalValue = dailyValues.get(dailyValues.size() - 1);
        double totalReturn = (finalValue - cfg.getInitCapital()) / cfg.getInitCapital();
        double annualReturn = totalReturn * (252.0 / data.size());

        // 夏普比率
        double[] dailyReturns = new double[dailyValues.size() - 1];
        for (int i = 1; i < dailyValues.size(); i++) {
            dailyReturns[i - 1] = dailyValues.get(i) / dailyValues.get(i - 1) - 1;
        }
        double meanRet = Arrays.stream(dailyReturns).average().orElse(0);
        double stdRet = Math.sqrt(Arrays.stream(dailyReturns)
            .map(r -> (r - meanRet) * (r - meanRet)).average().orElse(0));
        double sharpe = stdRet > 0 ? (annualReturn - RISK_FREE_RATE) / (stdRet * Math.sqrt(252)) : 0;

        // 胜率
        double winRate = tradeReturns.isEmpty() ? 0
            : (double) tradeReturns.stream().filter(r -> r > 0).count() / tradeReturns.size();

        int tradeCount = tradeReturns.size();

        // 无交易 → 惩罚
        if (tradeCount == 0) return -999;

        double tradeBonus = Math.min(tradeCount / 20.0, 1.0);
        return 0.35 * totalReturn + 0.25 * sharpe + 0.20 * (1 - maxDrawdown)
            + 0.10 * winRate + 0.10 * tradeBonus;
    }
}
