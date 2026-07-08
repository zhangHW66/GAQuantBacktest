package com.gaquant.backtest;

import com.gaquant.model.TradeRecord;
import java.util.List;
import java.util.Map;

/** 绩效评估 */
public class Evaluator {
    public Map<String, Double> evaluate(List<TradeRecord> trades, double initCapital) {
        return Map.of(
            "cumulativeReturn", 0.0,
            "annualReturn", 0.0,
            "maxDrawdown", 0.0,
            "winRate", 0.0,
            "profitLossRatio", 0.0,
            "sharpeRatio", 0.0,
            "tradeCount", 0.0
        );
    }
}
