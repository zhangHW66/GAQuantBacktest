package com.gaquant.model;

import java.time.LocalDate;

/**
 * 交易记录模型
 */
public class TradeRecord {
    private LocalDate buyDate;
    private double buyPrice;
    private LocalDate sellDate;
    private double sellPrice;
    private double profit;
    private double profitRate;
    private String signalType; // BUY / SELL

    public TradeRecord() {}

    public TradeRecord(LocalDate buyDate, double buyPrice, LocalDate sellDate, double sellPrice) {
        this.buyDate = buyDate;
        this.buyPrice = buyPrice;
        this.sellDate = sellDate;
        this.sellPrice = sellPrice;
        this.profit = sellPrice - buyPrice;
        this.profitRate = (sellPrice - buyPrice) / buyPrice;
    }

    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }

    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }

    public LocalDate getSellDate() { return sellDate; }
    public void setSellDate(LocalDate sellDate) { this.sellDate = sellDate; }

    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public double getProfitRate() { return profitRate; }
    public void setProfitRate(double profitRate) { this.profitRate = profitRate; }

    public String getSignalType() { return signalType; }
    public void setSignalType(String signalType) { this.signalType = signalType; }
}
