package com.gaquant.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 遗传算法染色体模型
 * 编码：指标启用 | 指标参数 | 指标权重 | 买入阈值 | 卖出阈值
 */
public class Chromosome {
    // 指标启用状态 (MA, MACD, RSI, KDJ, BOLL, ATR, CCI)
    private Map<String, Boolean> indicatorEnabled;

    // 指标参数 (如 MA: [5,10,20,60], RSI: [6,12,24])
    private Map<String, int[]> indicatorParams;

    // 指标权重 (各指标信号在最终决策中的权重)
    private Map<String, Double> indicatorWeights;

    // 买卖阈值
    private double buyThreshold;   // 综合得分 > 此值产生买入信号
    private double sellThreshold;  // 综合得分 < 此值产生卖出信号

    // 适应度
    private double fitness;

    public Chromosome() {
        this.indicatorEnabled = new HashMap<>();
        this.indicatorParams = new HashMap<>();
        this.indicatorWeights = new HashMap<>();
        this.fitness = 0.0;
    }

    public Map<String, Boolean> getIndicatorEnabled() { return indicatorEnabled; }
    public void setIndicatorEnabled(Map<String, Boolean> indicatorEnabled) { this.indicatorEnabled = indicatorEnabled; }

    public Map<String, int[]> getIndicatorParams() { return indicatorParams; }
    public void setIndicatorParams(Map<String, int[]> indicatorParams) { this.indicatorParams = indicatorParams; }

    public Map<String, Double> getIndicatorWeights() { return indicatorWeights; }
    public void setIndicatorWeights(Map<String, Double> indicatorWeights) { this.indicatorWeights = indicatorWeights; }

    public double getBuyThreshold() { return buyThreshold; }
    public void setBuyThreshold(double buyThreshold) { this.buyThreshold = buyThreshold; }

    public double getSellThreshold() { return sellThreshold; }
    public void setSellThreshold(double sellThreshold) { this.sellThreshold = sellThreshold; }

    public double getFitness() { return fitness; }
    public void setFitness(double fitness) { this.fitness = fitness; }
}
