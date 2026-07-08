package com.gaquant.model;

import java.util.*;

/**
 * 绩效评估结果
 * 包含统计指标、净值曲线、年度汇总
 */
public class EvaluationResult {
    // 统计指标
    private double cumulativeReturn;
    private double annualReturn;
    private double maxDrawdown;
    private double sharpeRatio;
    private double winRate;
    private double profitLossRatio;
    private int tradeCount;

    // 净值曲线（逐日累计资产价值）
    private List<Double> netValueCurve;

    // 年度汇总
    private List<AnnualSummary> annualSummaries;

    public static class AnnualSummary {
        private int year;
        private double return_;
        private double maxDrawdown;
        private int tradeCount;
        private double winRate;

        public AnnualSummary() {}

        public AnnualSummary(int year, double return_, double maxDrawdown, int tradeCount, double winRate) {
            this.year = year;
            this.return_ = return_;
            this.maxDrawdown = maxDrawdown;
            this.tradeCount = tradeCount;
            this.winRate = winRate;
        }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public double getReturn_() { return return_; }
        public void setReturn_(double return_) { this.return_ = return_; }
        public double getMaxDrawdown() { return maxDrawdown; }
        public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
        public int getTradeCount() { return tradeCount; }
        public void setTradeCount(int tradeCount) { this.tradeCount = tradeCount; }
        public double getWinRate() { return winRate; }
        public void setWinRate(double winRate) { this.winRate = winRate; }
    }

    // Getters & Setters
    public double getCumulativeReturn() { return cumulativeReturn; }
    public void setCumulativeReturn(double cumulativeReturn) { this.cumulativeReturn = cumulativeReturn; }
    public double getAnnualReturn() { return annualReturn; }
    public void setAnnualReturn(double annualReturn) { this.annualReturn = annualReturn; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
    public double getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(double sharpeRatio) { this.sharpeRatio = sharpeRatio; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public double getProfitLossRatio() { return profitLossRatio; }
    public void setProfitLossRatio(double profitLossRatio) { this.profitLossRatio = profitLossRatio; }
    public int getTradeCount() { return tradeCount; }
    public void setTradeCount(int tradeCount) { this.tradeCount = tradeCount; }
    public List<Double> getNetValueCurve() { return netValueCurve; }
    public void setNetValueCurve(List<Double> netValueCurve) { this.netValueCurve = netValueCurve; }
    public List<AnnualSummary> getAnnualSummaries() { return annualSummaries; }
    public void setAnnualSummaries(List<AnnualSummary> annualSummaries) { this.annualSummaries = annualSummaries; }
}
