package com.gaquant.config;

/**
 * 应用全局配置
 */
public class AppConfig {

    // 遗传算法参数
    private int populationSize = 100;
    private int maxIterations = 50;
    private double crossoverRate = 0.80;
    private double mutationRate = 0.10;
    private int eliteCount = 5;

    // 回测参数
    private double initCapital = 100000.0;
    private double commission = 0.0003;
    private double slippage = 0.0001;
    private double maxPosition = 0.30;

    // 滚动窗口参数
    private int trainPeriod = 3;    // 月（日线=63根，月线=3根）
    private int testPeriod = 1;     // 月（日线=21根，月线=1根）
    private int barsPerMonth = 21;  // 每月约合多少根K线（日线21，月线1）
    private int minDataSize = 100;

    // 数据源
    private String csvPath = "";
    private String dateFormat = "yyyy-MM-dd";

    // ---- 单例 ----
    private static final AppConfig INSTANCE = new AppConfig();

    private AppConfig() {}

    public static AppConfig getInstance() { return INSTANCE; }

    // ---- Getters & Setters ----
    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

    public int getMaxIterations() { return maxIterations; }
    public void setMaxIterations(int maxIterations) { this.maxIterations = maxIterations; }

    public double getCrossoverRate() { return crossoverRate; }
    public void setCrossoverRate(double crossoverRate) { this.crossoverRate = crossoverRate; }

    public double getMutationRate() { return mutationRate; }
    public void setMutationRate(double mutationRate) { this.mutationRate = mutationRate; }

    public int getEliteCount() { return eliteCount; }
    public void setEliteCount(int eliteCount) { this.eliteCount = eliteCount; }

    public double getInitCapital() { return initCapital; }
    public void setInitCapital(double initCapital) { this.initCapital = initCapital; }

    public double getCommission() { return commission; }
    public void setCommission(double commission) { this.commission = commission; }

    public double getSlippage() { return slippage; }
    public void setSlippage(double slippage) { this.slippage = slippage; }

    public double getMaxPosition() { return maxPosition; }
    public void setMaxPosition(double maxPosition) { this.maxPosition = maxPosition; }

    public int getTrainPeriod() { return trainPeriod; }
    public void setTrainPeriod(int trainPeriod) { this.trainPeriod = trainPeriod; }

    public int getTestPeriod() { return testPeriod; }
    public void setTestPeriod(int testPeriod) { this.testPeriod = testPeriod; }

    public int getMinDataSize() { return minDataSize; }
    public void setMinDataSize(int minDataSize) { this.minDataSize = minDataSize; }

    public int getBarsPerMonth() { return barsPerMonth; }
    public void setBarsPerMonth(int barsPerMonth) { this.barsPerMonth = barsPerMonth; }

    public String getCsvPath() { return csvPath; }
    public void setCsvPath(String csvPath) { this.csvPath = csvPath; }

    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
}
