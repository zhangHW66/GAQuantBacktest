package com.gaquant.service;

import com.gaquant.config.AppConfig;
import com.gaquant.model.Chromosome;
import com.gaquant.model.StockData;
import java.time.LocalDate;
import java.util.*;

/**
 * 滚动窗口预测服务
 * 日线: 3月(≈63根)训练 → 1月(≈21根)预测 → 平移1月
 * 月线: 3年(36根)训练 → 1年(12根)预测 → 平移1年
 * 训练集与测试集严格不重叠
 */
public class RollingService {

    /** 单轮窗口结果 */
    public static class WindowResult {
        public int round;
        public LocalDate trainStart, trainEnd, testStart, testEnd;
        public Chromosome bestChromosome;
        public List<String> signals; // BUY/SELL/HOLD

        public int getTrainBars() { return signals != null ? signals.size() : 0; }
    }

    /**
     * 执行滚动窗口预测
     * @param data   全量行情（按时序排列）
     * @param config 含 trainPeriod/testPeriod/barsPerMonth
     * @return 每轮窗口的结果列表
     */
    public List<WindowResult> run(List<StockData> data, AppConfig config) {
        int trainBars = config.getTrainPeriod() * config.getBarsPerMonth();
        int testBars  = config.getTestPeriod()  * config.getBarsPerMonth();
        int windowBars = trainBars + testBars;

        List<WindowResult> results = new ArrayList<>();
        int round = 1;

        for (int start = 0; start + windowBars <= data.size(); start += testBars) {
            int trainEnd  = start + trainBars;
            int testEnd   = trainEnd + testBars;

            List<StockData> trainSet = new ArrayList<>(data.subList(start, trainEnd));
            List<StockData> testSet  = new ArrayList<>(data.subList(trainEnd, testEnd));

            WindowResult wr = new WindowResult();
            wr.round        = round;
            wr.trainStart   = trainSet.get(0).getDate();
            wr.trainEnd     = trainSet.get(trainSet.size() - 1).getDate();
            wr.testStart    = testSet.get(0).getDate();
            wr.testEnd      = testSet.get(testSet.size() - 1).getDate();

            // TODO Step 3: GA寻优 → 最优染色体
            // GeneticAlgorithmService ga = new GeneticAlgorithmService();
            // wr.bestChromosome = ga.optimize(trainSet);
            wr.bestChromosome = new Chromosome(); // 占位

            // TODO Step 4: 策略信号生成
            // Strategy strategy = new Strategy();
            // wr.signals = strategy.generateSignals(testSet, wr.bestChromosome);
            wr.signals = List.of(); // 占位

            results.add(wr);
            round++;
        }
        return results;
    }

    /** 聚合所有窗口的信号（用于统一回测评估） */
    public List<String> aggregateSignals(List<WindowResult> results) {
        List<String> all = new ArrayList<>();
        for (WindowResult wr : results) {
            if (wr.signals != null) all.addAll(wr.signals);
        }
        return all;
    }
}
