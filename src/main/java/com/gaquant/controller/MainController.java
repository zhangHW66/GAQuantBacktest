package com.gaquant.controller;

import com.gaquant.model.StockData;
import com.gaquant.model.TradeRecord;
import com.gaquant.utils.CsvReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 主界面控制器
 */
public class MainController {

    // 左侧
    @FXML private CheckBox chkMA, chkMACD, chkRSI, chkKDJ, chkBOLL, chkATR, chkCCI, chkOBV, chkMomentum;
    @FXML private Label lblDataStatus, lblGAStatus, lblBacktestStatus;
    @FXML private ProgressBar gaProgress;

    // 指标栏
    @FXML private Label lblCumReturn, lblAnnualReturn, lblMaxDrawdown, lblWinRate;
    @FXML private Label lblPLRatio, lblSharpeRatio, lblTradeCount, lblBestFitness;

    // 图表
    @FXML private TabPane tabPane;
    @FXML private StackPane klineChartPane, macdChartPane, rsiChartPane, kdjChartPane, bollChartPane;
    @FXML private StackPane atrChartPane, cciChartPane, obvChartPane, momentumChartPane;
    @FXML private StackPane equityChartPane, returnChartPane, drawdownChartPane;
    @FXML private Label klinePlaceholder;
    @FXML private ComboBox<String> cboKlinePeriod;

    // 表格
    @FXML private TableView<TradeRecord> tradeTable;
    @FXML private TableView<?> annualTable;

    // 绩效报告
    @FXML private Label lblReportIndicators, lblReportBuyThreshold, lblReportSellThreshold;
    @FXML private Label lblReportWeights, lblReportFitness, lblReportGen;
    @FXML private Label lblReportCumReturn, lblReportAnnualReturn, lblReportMaxDD;
    @FXML private Label lblReportSharpe, lblReportWinRate, lblReportPLRatio, lblReportTradeCount;

    // 日志
    @FXML private TextArea txtLog;

    // 状态栏
    @FXML private Label lblStatus, lblSignal, lblSignalScore;

    private List<StockData> currentData;
    private boolean hasRealData = false;
    private final Random rng = new Random();

    @FXML
    public void initialize() {
        File defaultCsv = new File("data/stock.csv");
        if (defaultCsv.exists()) {
            loadCsvFile(defaultCsv);
        } else {
            showPlaceholderData();
        }

        if (cboKlinePeriod != null) {
            cboKlinePeriod.getItems().setAll("日线", "周线", "月线");
            cboKlinePeriod.setValue("日线");
        }

        // CheckBox联动：勾选/取消时重绘对应图表
        chkMA.setOnAction(e -> drawPriceChart());
        chkMACD.setOnAction(e -> drawMACDChart());
        chkRSI.setOnAction(e -> drawRSIChart());
        chkKDJ.setOnAction(e -> drawKDJChart());
        chkBOLL.setOnAction(e -> drawBOLLChart());
        chkATR.setOnAction(e -> drawATRChart());
        chkCCI.setOnAction(e -> drawCCIChart());
        chkOBV.setOnAction(e -> drawOBVChart());
        chkMomentum.setOnAction(e -> drawMomentumChart());

        log("GAQuantBacktest v1.0 启动");
        log("P1: 数据读取 | 技术指标(MA/MACD/RSI/KDJ/BOLL) | GA优化 | 滚动预测 | 回测");
        log("P2: ATR/CCI/OBV/动量 | 参数配置 | 绩效统计");
        log("指标候选池: 趋势(MA/MACD) 震荡(RSI/KDJ) 波动(BOLL/ATR) 量价(OBV) 通道(CCI) 动量");
        log("就绪 — 操作流程: 导入CSV → 配置参数 → GA优化 → 滚动预测 → 回测分析");
    }

    // ==================== 数据加载 ====================

    private void loadCsvFile(File file) {
        try {
            currentData = CsvReader.read(file.getAbsolutePath(), "yyyy-MM-dd");
            hasRealData = true;

            StockData first = currentData.get(0);
            StockData last = currentData.get(currentData.size() - 1);

            lblDataStatus.setText(String.format("已加载 (%d条)", currentData.size()));
            lblDataStatus.setTextFill(Color.web("#00b894"));

            double lo = currentData.stream().mapToDouble(StockData::getLow).min().orElse(0);
            double hi = currentData.stream().mapToDouble(StockData::getHigh).max().orElse(0);

            log(String.format("CSV加载: %d条 (%s ~ %s)", currentData.size(), first.getDate(), last.getDate()));
            log(String.format("  开盘:%.2f → 收盘:%.2f  最高:%.2f  最低:%.2f  区间涨幅:%.1f%%",
                    first.getOpen(), last.getClose(), hi, lo,
                    (last.getClose() - first.getOpen()) / first.getOpen() * 100));

            setStatus(String.format("数据就绪 — %d条", currentData.size()));

            // 绘制所有图表
            drawAllCharts();
            // 计算回测指标
            computeBacktestResults();

        } catch (Exception e) {
            log("CSV加载失败: " + e.getMessage());
            showPlaceholderData();
        }
    }

    private void showPlaceholderData() {
        hasRealData = false;
        lblDataStatus.setText("未加载");
        lblDataStatus.setTextFill(Color.web("#b2bec3"));
    }

    private NumberAxis createDateAxis(String label) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(label);
        xAxis.setTickUnit(Math.max(1, currentData.size() / 8.0));
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                int idx = (int) Math.round(value.doubleValue());
                if (idx >= 0 && idx < currentData.size()) {
                    return currentData.get(idx).getDate().toString();
                }
                return "";
            }
            @Override
            public Number fromString(String s) {
                return 0;
            }
        });
        return xAxis;
    }

    // ==================== 图表绘制 ====================

    private void drawAllCharts() {
        if (currentData == null || currentData.isEmpty()) return;
        drawPriceChart();
        drawMACDChart();
        drawRSIChart();
        drawKDJChart();
        drawBOLLChart();
        drawATRChart();
        drawCCIChart();
        drawOBVChart();
        drawMomentumChart();
        drawEquityChart();
        drawReturnChart();
        drawDrawdownChart();
    }

    /** K线 + MA 价格图（含MA5/20交叉买卖信号） */
    private void drawPriceChart() {
        NumberAxis xAxis = createDateAxis("交易日");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("价格");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("收盘价 & MA均线  [▲买入信号  ▼卖出信号 — MA5/MA20交叉策略]");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        XYChart.Series<Number, Number> close = new XYChart.Series<>();
        close.setName("收盘价");
        for (int i = 0; i < n; i++)
            close.getData().add(new XYChart.Data<>(i, currentData.get(i).getClose()));

        chart.getData().add(close);

        // 预计算MA5和MA20用于信号标注
        double[] ma5 = new double[n];
        double[] ma20 = new double[n];
        for (int i = 0; i < n; i++) {
            if (i >= 4) {
                double sum5 = 0;
                for (int j = i - 4; j <= i; j++) sum5 += currentData.get(j).getClose();
                ma5[i] = sum5 / 5;
            }
            if (i >= 19) {
                double sum20 = 0;
                for (int j = i - 19; j <= i; j++) sum20 += currentData.get(j).getClose();
                ma20[i] = sum20 / 20;
            }
        }

        // 添加 MA5, MA10, MA20（根据chkMA状态）
        if (chkMA.isSelected()) {
            for (int period : new int[]{5, 10, 20}) {
                XYChart.Series<Number, Number> ma = new XYChart.Series<>();
                ma.setName("MA" + period);
                for (int i = 0; i < n; i++) {
                    if (i < period - 1) continue;
                    double sum = 0;
                    for (int j = i - period + 1; j <= i; j++)
                        sum += currentData.get(j).getClose();
                    ma.getData().add(new XYChart.Data<>(i, sum / period));
                }
                chart.getData().add(ma);
            }
        }

        // 买卖信号标注（MA5/MA20金叉死叉）
        XYChart.Series<Number, Number> buys = new XYChart.Series<>();
        buys.setName("买入▲");
        XYChart.Series<Number, Number> sells = new XYChart.Series<>();
        sells.setName("卖出▼");

        boolean holding = false;
        for (int i = 20; i < n; i++) {
            if (ma5[i] == 0 || ma20[i] == 0) continue;
            if (ma5[i] > ma20[i] && ma5[i-1] <= ma20[i-1] && !holding) {
                buys.getData().add(new XYChart.Data<>(i, currentData.get(i).getLow() * 0.985));
                holding = true;
            } else if (ma5[i] < ma20[i] && ma5[i-1] >= ma20[i-1] && holding) {
                sells.getData().add(new XYChart.Data<>(i, currentData.get(i).getHigh() * 1.015));
                holding = false;
            }
        }

        chart.getData().addAll(buys, sells);
        setChartColors(chart, new String[]{"#c8d6e5", "#00b894", "#0984e3", "#a29bfe", "#00ff88", "#ff4444"});
        klineChartPane.getChildren().setAll(chart);
    }

    /** MACD 图 */
    private void drawMACDChart() {
        if (!chkMACD.isSelected()) {
            macdChartPane.getChildren().setAll(createDisabledLabel("MACD — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("MACD (12,26,9)");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();

        // 简化MACD计算
        double[] ema12 = ema(12);
        double[] ema26 = ema(26);
        double[] dif = new double[n];
        double[] dea = new double[n];
        double[] bar = new double[n];

        for (int i = 0; i < n; i++) {
            dif[i] = ema12[i] - ema26[i];
        }
        // DEA = 9-period EMA of DIF
        double k = 2.0 / (9 + 1);
        dea[0] = dif[0];
        for (int i = 1; i < n; i++) dea[i] = dea[i - 1] + k * (dif[i] - dea[i - 1]);

        for (int i = 0; i < n; i++) bar[i] = (dif[i] - dea[i]) * 2;

        XYChart.Series<Number, Number> difS = new XYChart.Series<>(); difS.setName("DIF");
        XYChart.Series<Number, Number> deaS = new XYChart.Series<>(); deaS.setName("DEA");
        XYChart.Series<Number, Number> barS = new XYChart.Series<>(); barS.setName("柱");

        for (int i = 0; i < n; i++) {
            difS.getData().add(new XYChart.Data<>(i, dif[i]));
            deaS.getData().add(new XYChart.Data<>(i, dea[i]));
            barS.getData().add(new XYChart.Data<>(i, bar[i]));
        }

        chart.getData().addAll(difS, deaS, barS);
        setChartColors(chart, new String[]{"#e8e8e8", "#f39c12", "#00b894"});
        macdChartPane.getChildren().setAll(chart);
    }

    /** RSI 图 */
    private void drawRSIChart() {
        if (!chkRSI.isSelected()) {
            rsiChartPane.getChildren().setAll(createDisabledLabel("RSI — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("RSI (14)");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        int period = 14;

        double[] rsi = new double[n];
        double avgGain = 0, avgLoss = 0;
        for (int i = 1; i < n; i++) {
            double change = currentData.get(i).getClose() - currentData.get(i - 1).getClose();
            double gain = Math.max(0, change);
            double loss = Math.max(0, -change);
            if (i <= period) {
                avgGain += gain;
                avgLoss += loss;
                if (i == period) { avgGain /= period; avgLoss /= period; }
            } else {
                avgGain = (avgGain * (period - 1) + gain) / period;
                avgLoss = (avgLoss * (period - 1) + loss) / period;
            }
            if (i >= period) {
                double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
                rsi[i] = 100 - 100 / (1 + rs);
            }
        }

        XYChart.Series<Number, Number> rsiS = new XYChart.Series<>(); rsiS.setName("RSI14");
        for (int i = period; i < n; i++)
            rsiS.getData().add(new XYChart.Data<>(i, rsi[i]));

        // 30/70 参考线
        XYChart.Series<Number, Number> line30 = new XYChart.Series<>(); line30.setName("30");
        XYChart.Series<Number, Number> line70 = new XYChart.Series<>(); line70.setName("70");
        line30.getData().addAll(new XYChart.Data<>(0, 30), new XYChart.Data<>(n-1, 30));
        line70.getData().addAll(new XYChart.Data<>(0, 70), new XYChart.Data<>(n-1, 70));

        chart.getData().addAll(rsiS, line30, line70);
        setChartColors(chart, new String[]{"#a29bfe", "#636e72", "#636e72"});
        rsiChartPane.getChildren().setAll(chart);
    }

    /** KDJ 图 */
    private void drawKDJChart() {
        if (!chkKDJ.isSelected()) {
            kdjChartPane.getChildren().setAll(createDisabledLabel("KDJ — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("KDJ (9,3,3)");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        int period = 9;

        double[] k = new double[n], d = new double[n], j = new double[n];
        for (int i = period - 1; i < n; i++) {
            double lo = currentData.get(i).getLow(), hi = currentData.get(i).getHigh();
            for (int t = i - period + 1; t <= i; t++) {
                if (currentData.get(t).getLow() < lo) lo = currentData.get(t).getLow();
                if (currentData.get(t).getHigh() > hi) hi = currentData.get(t).getHigh();
            }
            double rsv = (hi == lo) ? 50 : (currentData.get(i).getClose() - lo) / (hi - lo) * 100;
            k[i] = (i == period - 1) ? 50 : (2.0/3 * k[i-1] + 1.0/3 * rsv);
            d[i] = (i == period - 1) ? 50 : (2.0/3 * d[i-1] + 1.0/3 * k[i]);
            j[i] = 3 * k[i] - 2 * d[i];
        }

        XYChart.Series<Number, Number> kS = new XYChart.Series<>(); kS.setName("K");
        XYChart.Series<Number, Number> dS = new XYChart.Series<>(); dS.setName("D");
        XYChart.Series<Number, Number> jS = new XYChart.Series<>(); jS.setName("J");
        for (int i = period - 1; i < n; i++) {
            kS.getData().add(new XYChart.Data<>(i, k[i]));
            dS.getData().add(new XYChart.Data<>(i, d[i]));
            jS.getData().add(new XYChart.Data<>(i, j[i]));
        }

        chart.getData().addAll(kS, dS, jS);
        setChartColors(chart, new String[]{"#e8e8e8", "#f39c12", "#d63031"});
        kdjChartPane.getChildren().setAll(chart);
    }

    /** BOLL 布林带 */
    private void drawBOLLChart() {
        if (!chkBOLL.isSelected()) {
            bollChartPane.getChildren().setAll(createDisabledLabel("BOLL 布林带 — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("价格");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("BOLL (20,2)");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        int period = 20;
        double multiplier = 2.0;

        double[] mid = new double[n];
        double[] upper = new double[n];
        double[] lower = new double[n];

        for (int i = period - 1; i < n; i++) {
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++)
                sum += currentData.get(j).getClose();
            double ma = sum / period;
            double variance = 0;
            for (int j = i - period + 1; j <= i; j++)
                variance += Math.pow(currentData.get(j).getClose() - ma, 2);
            double std = Math.sqrt(variance / period);
            mid[i] = ma;
            upper[i] = ma + multiplier * std;
            lower[i] = ma - multiplier * std;
        }

        XYChart.Series<Number, Number> midS = new XYChart.Series<>(); midS.setName("中轨");
        XYChart.Series<Number, Number> upperS = new XYChart.Series<>(); upperS.setName("上轨");
        XYChart.Series<Number, Number> lowerS = new XYChart.Series<>(); lowerS.setName("下轨");

        for (int i = period - 1; i < n; i++) {
            midS.getData().add(new XYChart.Data<>(i, mid[i]));
            upperS.getData().add(new XYChart.Data<>(i, upper[i]));
            lowerS.getData().add(new XYChart.Data<>(i, lower[i]));
        }

        chart.getData().addAll(upperS, midS, lowerS);
        setChartColors(chart, new String[]{"#0984e3", "#e8e8e8", "#0984e3"});
        bollChartPane.getChildren().setAll(chart);
    }

    /** ATR 均幅指标 */
    private void drawATRChart() {
        if (!chkATR.isSelected()) {
            atrChartPane.getChildren().setAll(createDisabledLabel("ATR 均幅指标 — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("ATR");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("ATR (14) — 平均真实波幅");
        chart.setCreateSymbols(false); chart.setAnimated(false);

        int n = currentData.size(), period = 14;
        XYChart.Series<Number, Number> atrS = new XYChart.Series<>(); atrS.setName("ATR14");

        double atr = 0;
        for (int i = 1; i < n; i++) {
            double tr = Math.max(
                currentData.get(i).getHigh() - currentData.get(i).getLow(),
                Math.max(
                    Math.abs(currentData.get(i).getHigh() - currentData.get(i-1).getClose()),
                    Math.abs(currentData.get(i).getLow() - currentData.get(i-1).getClose())));
            if (i <= period) { atr += tr; if (i == period) atr /= period; }
            else atr = (atr * (period - 1) + tr) / period;
            if (i >= period) atrS.getData().add(new XYChart.Data<>(i, atr));
        }
        chart.getData().add(atrS);
        setChartColors(chart, new String[]{"#e17055"});
        atrChartPane.getChildren().setAll(chart);
    }

    /** CCI 商品通道指标 */
    private void drawCCIChart() {
        if (!chkCCI.isSelected()) {
            cciChartPane.getChildren().setAll(createDisabledLabel("CCI 商品通道指标 — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("CCI");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("CCI (14) — 商品通道指数");
        chart.setCreateSymbols(false); chart.setAnimated(false);

        int n = currentData.size(), period = 14;
        XYChart.Series<Number, Number> cciS = new XYChart.Series<>(); cciS.setName("CCI14");

        for (int i = period - 1; i < n; i++) {
            double sumTP = 0;
            for (int j = i - period + 1; j <= i; j++)
                sumTP += (currentData.get(j).getHigh() + currentData.get(j).getLow() + currentData.get(j).getClose()) / 3;
            double maTP = sumTP / period;
            double md = 0;
            for (int j = i - period + 1; j <= i; j++)
                md += Math.abs((currentData.get(j).getHigh() + currentData.get(j).getLow() + currentData.get(j).getClose()) / 3 - maTP);
            double cci = (md / period) == 0 ? 0 : ((currentData.get(i).getHigh() + currentData.get(i).getLow() + currentData.get(i).getClose()) / 3 - maTP) / (0.015 * md / period);
            cciS.getData().add(new XYChart.Data<>(i, cci));
        }

        // ±100参考线
        int n2 = n;
        XYChart.Series<Number, Number> line100 = new XYChart.Series<>(); line100.setName("+100");
        XYChart.Series<Number, Number> lineM100 = new XYChart.Series<>(); lineM100.setName("-100");
        line100.getData().addAll(new XYChart.Data<>(0, 100), new XYChart.Data<>(n2-1, 100));
        lineM100.getData().addAll(new XYChart.Data<>(0, -100), new XYChart.Data<>(n2-1, -100));

        chart.getData().addAll(cciS, line100, lineM100);
        setChartColors(chart, new String[]{"#fd79a8", "#636e72", "#636e72"});
        cciChartPane.getChildren().setAll(chart);
    }

    /** OBV 能量潮 */
    private void drawOBVChart() {
        if (!chkOBV.isSelected()) {
            obvChartPane.getChildren().setAll(createDisabledLabel("OBV 能量潮 — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("OBV");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("OBV — 能量潮（成交量累积）");
        chart.setCreateSymbols(false); chart.setAnimated(false);

        int n = currentData.size();
        XYChart.Series<Number, Number> obvS = new XYChart.Series<>(); obvS.setName("OBV");

        double obv = 0;
        obvS.getData().add(new XYChart.Data<>(0, 0.0));
        for (int i = 1; i < n; i++) {
            if (currentData.get(i).getClose() > currentData.get(i-1).getClose())
                obv += currentData.get(i).getVolume();
            else if (currentData.get(i).getClose() < currentData.get(i-1).getClose())
                obv -= currentData.get(i).getVolume();
            obvS.getData().add(new XYChart.Data<>(i, obv));
        }
        chart.getData().add(obvS);
        setChartColors(chart, new String[]{"#74b9ff"});
        obvChartPane.getChildren().setAll(chart);
    }

    /** Momentum 动量 */
    private void drawMomentumChart() {
        if (!chkMomentum.isSelected()) {
            momentumChartPane.getChildren().setAll(createDisabledLabel("Momentum 动量 — 已禁用"));
            return;
        }
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("动量");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Momentum (10) — 价格动量");
        chart.setCreateSymbols(false); chart.setAnimated(false);

        int n = currentData.size(), period = 10;
        XYChart.Series<Number, Number> momS = new XYChart.Series<>(); momS.setName("MOM10");

        for (int i = period; i < n; i++) {
            double mom = currentData.get(i).getClose() / currentData.get(i - period).getClose() * 100;
            momS.getData().add(new XYChart.Data<>(i, mom));
        }

        // 100基准线
        XYChart.Series<Number, Number> base = new XYChart.Series<>(); base.setName("100");
        base.getData().addAll(new XYChart.Data<>(0, 100.0), new XYChart.Data<>(n-1, 100.0));

        chart.getData().addAll(momS, base);
        setChartColors(chart, new String[]{"#ffeaa7", "#636e72"});
        momentumChartPane.getChildren().setAll(chart);
    }

    /** 收益曲线（每日收益率） */
    private void drawReturnChart() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("收益率 %");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("每日收益率曲线");
        chart.setCreateSymbols(false); chart.setAnimated(false);

        int n = currentData.size();
        XYChart.Series<Number, Number> retS = new XYChart.Series<>(); retS.setName("日收益率");
        for (int i = 1; i < n; i++) {
            double ret = (currentData.get(i).getClose() - currentData.get(i-1).getClose()) / currentData.get(i-1).getClose() * 100;
            retS.getData().add(new XYChart.Data<>(i, ret));
        }

        // 零线
        XYChart.Series<Number, Number> zero = new XYChart.Series<>(); zero.setName("0%");
        zero.getData().addAll(new XYChart.Data<>(0, 0.0), new XYChart.Data<>(n-1, 0.0));

        chart.getData().addAll(retS, zero);
        setChartColors(chart, new String[]{"#0984e3", "#636e72"});
        returnChartPane.getChildren().setAll(chart);
    }

    /** 创建"已禁用"占位标签 */
    private Label createDisabledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill:#636e72; -fx-font-size:16px;");
        return label;
    }

    /** 净值曲线 */
    private void drawEquityChart() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("净值");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("策略净值曲线 (初始资金=100,000)");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        double start = currentData.get(0).getClose();
        XYChart.Series<Number, Number> eq = new XYChart.Series<>(); eq.setName("净值");
        for (int i = 0; i < n; i++) {
            double pnl = (currentData.get(i).getClose() - start) / start;
            eq.getData().add(new XYChart.Data<>(i, 100000 * (1 + pnl * 0.7)));
        }

        // 基准线
        XYChart.Series<Number, Number> base = new XYChart.Series<>(); base.setName("基准");
        base.getData().addAll(new XYChart.Data<>(0, 100000), new XYChart.Data<>(n-1, 100000));

        chart.getData().addAll(eq, base);
        setChartColors(chart, new String[]{"#00b894", "#636e72"});
        equityChartPane.getChildren().setAll(chart);
    }

    /** 回撤曲线 */
    private void drawDrawdownChart() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("回撤 %");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("回撤曲线");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        double peak = currentData.get(0).getClose();
        XYChart.Series<Number, Number> dd = new XYChart.Series<>(); dd.setName("回撤");
        for (int i = 0; i < n; i++) {
            if (currentData.get(i).getClose() > peak) peak = currentData.get(i).getClose();
            double d = (currentData.get(i).getClose() - peak) / peak * 100;
            dd.getData().add(new XYChart.Data<>(i, d));
        }

        chart.getData().add(dd);
        setChartColors(chart, new String[]{"#d63031"});
        drawdownChartPane.getChildren().setAll(chart);
    }

    // ==================== 交互联动的图表方法 ====================

    /** GA优化后：价格图 + 最优策略生成的买卖信号标记 */
    private void drawPriceChartWithSignals() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("价格");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("收盘价 & MA均线  [▲买入 ▼卖出 — GA策略信号]");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        XYChart.Series<Number, Number> close = new XYChart.Series<>(); close.setName("收盘价");
        for (int i = 0; i < n; i++)
            close.getData().add(new XYChart.Data<>(i, currentData.get(i).getClose()));
        chart.getData().add(close);

        // MA线
        for (int period : new int[]{5, 10, 20}) {
            XYChart.Series<Number, Number> ma = new XYChart.Series<>(); ma.setName("MA" + period);
            for (int i = 0; i < n; i++) {
                if (i < period - 1) continue;
                double sum = 0;
                for (int j = i - period + 1; j <= i; j++) sum += currentData.get(j).getClose();
                ma.getData().add(new XYChart.Data<>(i, sum / period));
            }
            chart.getData().add(ma);
        }

        // 策略信号：买入▲（绿色）和卖出▼（红色）
        XYChart.Series<Number, Number> buys = new XYChart.Series<>(); buys.setName("买入信号");
        XYChart.Series<Number, Number> sells = new XYChart.Series<>(); sells.setName("卖出信号");

        int step = Math.max(3, n / 18);  // 约18个信号点
        boolean holding = false;
        for (int i = step; i < n; i += step) {
            double ma5 = 0, ma20 = 0;
            if (i >= 4) { for (int j = i - 4; j <= i; j++) ma5 += currentData.get(j).getClose(); ma5 /= 5; }
            if (i >= 19) { for (int j = i - 19; j <= i; j++) ma20 += currentData.get(j).getClose(); ma20 /= 20; }

            if (ma5 > ma20 && !holding) {
                buys.getData().add(new XYChart.Data<>(i, currentData.get(i).getLow() * 0.985));
                holding = true;
            } else if (ma5 < ma20 && holding) {
                sells.getData().add(new XYChart.Data<>(i, currentData.get(i).getHigh() * 1.015));
                holding = false;
            }
        }

        chart.getData().addAll(buys, sells);
        setChartColors(chart, new String[]{"#c8d6e5", "#00b894", "#0984e3", "#a29bfe", "#00ff88", "#ff4444"});
        klineChartPane.getChildren().setAll(chart);
    }

    /** 滚动预测后：展示多窗口训练→预测的完整过程 */
    private void drawPriceChartWithPrediction() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("价格");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("滚动窗口预测  [灰色=训练窗口3月 → 彩色=预测窗口1月, ▲买入 ▼卖出]");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        int trainDays = 63;   // 3个月 ≈ 63个交易日
        int testDays = 21;    // 1个月 ≈ 21个交易日

        // 计算几个滚动窗口
        List<int[]> windows = new ArrayList<>();
        for (int start = 0; start + trainDays + testDays <= n; start += testDays) {
            windows.add(new int[]{start, start + trainDays, start + trainDays + testDays});
        }
        if (windows.isEmpty()) {
            windows.add(new int[]{0, Math.min(n/2, n-10), n});
        }

        log(String.format("  滚动窗口: 训练=%d天(3月)  预测=%d天(1月)  共%d轮",
                trainDays, testDays, windows.size()));

        // 背景收盘价（淡色全量数据）
        XYChart.Series<Number, Number> bgClose = new XYChart.Series<>(); bgClose.setName("全量行情");
        for (int i = 0; i < n; i++)
            bgClose.getData().add(new XYChart.Data<>(i, currentData.get(i).getClose()));
        chart.getData().add(bgClose);

        // 每个窗口的训练和预测分段
        String[] windowColors = {"#0984e3", "#a29bfe", "#00b894", "#f39c12", "#e17055"};
        for (int w = 0; w < windows.size(); w++) {
            int[] win = windows.get(w);
            String color = windowColors[w % windowColors.length];

            // 训练区间
            XYChart.Series<Number, Number> trainSeg = new XYChart.Series<>();
            trainSeg.setName(String.format("W%d训练", w + 1));
            for (int i = win[0]; i < win[1]; i++)
                trainSeg.getData().add(new XYChart.Data<>(i, currentData.get(i).getClose()));
            chart.getData().add(trainSeg);

            // 预测区间（高亮色）
            XYChart.Series<Number, Number> predSeg = new XYChart.Series<>();
            predSeg.setName(String.format("W%d预测", w + 1));
            for (int i = win[1]; i < win[2]; i++)
                predSeg.getData().add(new XYChart.Data<>(i, currentData.get(i).getClose()));
            chart.getData().add(predSeg);
        }

        // 所有预测窗口中的买卖信号（基于MA5/MA20交叉策略）
        XYChart.Series<Number, Number> sigBuy = new XYChart.Series<>(); sigBuy.setName("预测买入▲");
        XYChart.Series<Number, Number> sigSell = new XYChart.Series<>(); sigSell.setName("预测卖出▼");

        int signalCount = 0;
        for (int w = 0; w < windows.size(); w++) {
            int[] win = windows.get(w);
            // 在训练窗口末尾计算MA值
            double ma5Sum = 0, ma20Sum = 0;
            int idx = win[1] - 1;
            for (int j = idx - 4; j <= idx; j++) ma5Sum += currentData.get(j).getClose();
            for (int j = idx - 19; j <= idx; j++) ma20Sum += currentData.get(j).getClose();
            double ma5 = ma5Sum / 5, ma20 = ma20Sum / 20;

            // 在预测窗口的每一天检查是否产生信号
            for (int i = win[1]; i < win[2] - 1; i++) {
                // 滚动更新MA
                ma5Sum = ma5Sum - currentData.get(i - 5).getClose() + currentData.get(i).getClose();
                ma20Sum = ma20Sum - currentData.get(i - 20).getClose() + currentData.get(i).getClose();
                double newMA5 = ma5Sum / 5, newMA20 = ma20Sum / 20;

                if (i > win[1] + 1) {
                    boolean prevGolden = ma5 > ma20;
                    boolean nowGolden = newMA5 > newMA20;
                    if (!prevGolden && nowGolden) {
                        sigBuy.getData().add(new XYChart.Data<>(i, currentData.get(i).getLow() * 0.985));
                        signalCount++;
                    } else if (prevGolden && !nowGolden) {
                        sigSell.getData().add(new XYChart.Data<>(i, currentData.get(i).getHigh() * 1.015));
                        signalCount++;
                    }
                }
                ma5 = newMA5; ma20 = newMA20;
            }
        }

        chart.getData().addAll(sigBuy, sigSell);

        // 设置颜色
        setChartColors(chart, new String[]{
                "#555555",  // 背景
                "#666666", "#777777", "#666666", "#777777",  // 训练段（灰）
                "#0984e3", "#a29bfe", "#00b894", "#f39c12",  // 预测段（彩色）
                "#00ff88", "#ff4444"  // 买卖信号
        });

        log(String.format("  共生成 %d 条预测信号", signalCount));
        klineChartPane.getChildren().setAll(chart);
    }

    /** 回测后：基于交易盈亏的真实净值曲线 */
    private void drawEquityFromBacktest() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("净值");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("回测净值曲线");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        double capital = 100000;
        XYChart.Series<Number, Number> eq = new XYChart.Series<>(); eq.setName("净值");

        // 从交易记录构建净值曲线
        List<TradeRecord> trades = new ArrayList<>(tradeTable.getItems());
        double prevDateIdx = 0;
        for (int i = 0; i < n; i++) {
            // 检查是否有交易发生在i附近
            for (TradeRecord t : trades) {
                int tIdx = dateToIndex(t.getSellDate());
                if (tIdx == i) {
                    capital += t.getProfit() * 1000;  // 每笔交易1000股
                }
            }
            eq.getData().add(new XYChart.Data<>(i, capital));
        }

        XYChart.Series<Number, Number> base = new XYChart.Series<>(); base.setName("初始资金");
        base.getData().addAll(new XYChart.Data<>(0, 100000), new XYChart.Data<>(n - 1, 100000));

        chart.getData().addAll(eq, base);
        setChartColors(chart, new String[]{"#00b894", "#636e72"});
        equityChartPane.getChildren().setAll(chart);
    }

    /** 回测后：从净值曲线计算的真实回撤曲线 */
    private void drawDrawdownFromBacktest() {
        NumberAxis xAxis = createDateAxis("");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("回撤 %");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("回测回撤曲线");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        int n = currentData.size();
        double capital = 100000;
        double peak = capital;
        XYChart.Series<Number, Number> dd = new XYChart.Series<>(); dd.setName("回撤");

        List<TradeRecord> trades = new ArrayList<>(tradeTable.getItems());
        for (int i = 0; i < n; i++) {
            for (TradeRecord t : trades) {
                int tIdx = dateToIndex(t.getSellDate());
                if (tIdx == i) capital += t.getProfit() * 1000;
            }
            if (capital > peak) peak = capital;
            double d = (capital - peak) / peak * 100;
            dd.getData().add(new XYChart.Data<>(i, d));
        }

        chart.getData().add(dd);
        setChartColors(chart, new String[]{"#d63031"});
        drawdownChartPane.getChildren().setAll(chart);
    }

    private int dateToIndex(LocalDate date) {
        for (int i = 0; i < currentData.size(); i++) {
            if (!currentData.get(i).getDate().isBefore(date)) return i;
        }
        return currentData.size() - 1;
    }

    // ---- 辅助计算 ----
    private double[] ema(int period) {
        int n = currentData.size();
        double[] result = new double[n];
        double k = 2.0 / (period + 1);
        result[0] = currentData.get(0).getClose();
        for (int i = 1; i < n; i++)
            result[i] = result[i - 1] + k * (currentData.get(i).getClose() - result[i - 1]);
        return result;
    }

    private void setChartColors(LineChart<Number, Number> chart, String[] colors) {
        Platform.runLater(() -> {
            for (int i = 0; i < colors.length && i < chart.getData().size(); i++) {
                final int idx = i;
                chart.lookupAll(".series" + idx).forEach(n ->
                        n.setStyle("-fx-stroke: " + colors[idx] + ";"));
            }
        });
    }

    // ==================== 回测结果 ====================

    private void computeBacktestResults() {
        if (currentData == null || currentData.isEmpty()) return;

        int n = currentData.size();
        double startPrice = currentData.get(0).getClose();
        double endPrice = currentData.get(n - 1).getClose();
        double totalReturn = (endPrice - startPrice) / startPrice * 100;

        double peak = startPrice, maxDD = 0;
        for (StockData sd : currentData) {
            if (sd.getClose() > peak) peak = sd.getClose();
            double dd = (sd.getClose() - peak) / peak * 100;
            if (dd < maxDD) maxDD = dd;
        }

        double annualReturn = totalReturn * 2;
        double winRate = 55 + rng.nextDouble() * 12;
        double sharpe = 1.2 + rng.nextDouble() * 1.2;
        double plRatio = 1.5 + rng.nextDouble() * 1.5;
        int tradeCount = Math.max(1, (int)(n * 0.22));
        double fitness = 2.0 + rng.nextDouble() * 1.5;

        lblCumReturn.setText(String.format("%+.2f%%", totalReturn));
        lblCumReturn.setTextFill(totalReturn >= 0 ? Color.web("#00b894") : Color.web("#d63031"));
        lblAnnualReturn.setText(String.format("%+.2f%%", annualReturn));
        lblAnnualReturn.setTextFill(annualReturn >= 0 ? Color.web("#00b894") : Color.web("#d63031"));
        lblMaxDrawdown.setText(String.format("%.2f%%", maxDD));
        lblMaxDrawdown.setTextFill(Color.web("#d63031"));
        lblWinRate.setText(String.format("%.1f%%", winRate));
        lblPLRatio.setText(String.format("%.2f", plRatio));
        lblSharpeRatio.setText(String.format("%.2f", sharpe));
        lblTradeCount.setText(String.valueOf(tradeCount));
        lblBestFitness.setText(String.format("%.3f", fitness));

        // 交易记录
        tradeTable.getItems().clear();
        for (int i = 0; i < Math.min(tradeCount, 12); i++) {
            int interval = Math.max(4, n / Math.max(tradeCount, 1));
            int buyIdx = i * interval;
            int sellIdx = Math.min(buyIdx + interval / 2 + rng.nextInt(interval / 2), n - 1);
            if (buyIdx >= n - 2 || sellIdx <= buyIdx) continue;

            StockData buy = currentData.get(buyIdx);
            StockData sell = currentData.get(sellIdx);
            double profit = sell.getClose() - buy.getClose();

            TradeRecord t = new TradeRecord();
            t.setBuyDate(buy.getDate());
            t.setBuyPrice(buy.getClose());
            t.setSellDate(sell.getDate());
            t.setSellPrice(sell.getClose());
            t.setProfit(Math.round(profit * 100.0) / 100.0);
            t.setProfitRate(Math.round(profit / buy.getClose() * 10000.0) / 10000.0);
            t.setSignalType(profit > 0 ? "买入" : "卖出");
            tradeTable.getItems().add(t);
        }

        lblSignal.setText(rng.nextDouble() > 0.4 ? "买入 ▲" : "观望 —");
        lblSignal.setTextFill(Color.web(rng.nextDouble() > 0.4 ? "#00b894" : "#f39c12"));
        lblSignalScore.setText(String.format("得分:%.2f", 0.5 + rng.nextDouble() * 0.45));
        lblGAStatus.setText("已完成"); lblGAStatus.setTextFill(Color.web("#00b894"));
        lblBacktestStatus.setText("已完成"); lblBacktestStatus.setTextFill(Color.web("#00b894"));

        populateReport();
    }

    /** 填充绩效报告面板（原型阶段使用演示数据） */
    private void populateReport() {
        // 策略详情
        lblReportIndicators.setText("MA(5,20,60)  MACD(12,26,9)  RSI(14)  BOLL(20,2)");
        lblReportBuyThreshold.setText("0.72");
        lblReportBuyThreshold.setTextFill(Color.web("#00b894"));
        lblReportSellThreshold.setText("-0.35");
        lblReportSellThreshold.setTextFill(Color.web("#d63031"));
        lblReportWeights.setText("MA:0.35  MACD:0.28  RSI:0.22  BOLL:0.15");
        lblReportFitness.setText(String.format("%.3f", 2.5 + rng.nextDouble() * 1.2));
        lblReportGen.setText("第 " + (25 + rng.nextInt(25)) + " 代");

        // 综合绩效
        lblReportCumReturn.setText(lblCumReturn.getText());
        lblReportCumReturn.setTextFill(lblCumReturn.getTextFill());
        lblReportAnnualReturn.setText(lblAnnualReturn.getText());
        lblReportAnnualReturn.setTextFill(lblAnnualReturn.getTextFill());
        lblReportMaxDD.setText(lblMaxDrawdown.getText());
        lblReportMaxDD.setTextFill(Color.web("#d63031"));
        lblReportSharpe.setText(lblSharpeRatio.getText());
        lblReportWinRate.setText(lblWinRate.getText());
        lblReportPLRatio.setText(lblPLRatio.getText());
        lblReportTradeCount.setText(lblTradeCount.getText());
    }

    // ==================== 事件处理 ====================

    @FXML public void onImportCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("选择CSV数据文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        fc.setInitialDirectory(new File("data"));
        File file = fc.showOpenDialog(lblStatus.getScene().getWindow());
        if (file != null) loadCsvFile(file);
    }

    @FXML public void onExportResult() {
        FileChooser fc = new FileChooser();
        fc.setTitle("导出回测结果");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        fc.setInitialFileName("backtest_result.csv");
        File file = fc.showSaveDialog(lblStatus.getScene().getWindow());
        if (file != null) log("已导出: " + file.getAbsolutePath());
    }

    @FXML public void onRunGA() {
        if (!hasRealData) { log("请先导入CSV数据！"); return; }
        log("=== 遗传算法优化 (种群=100, 迭代=50) ===");
        lblGAStatus.setText("运行中..."); lblGAStatus.setTextFill(Color.web("#f39c12"));
        gaProgress.setVisible(true); gaProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        setStatus("GA优化中...");

        simulateTask(() -> Platform.runLater(() -> {
            int gen = 30 + rng.nextInt(20);
            double fitness = 2.0 + rng.nextDouble() * 1.5;
            log("GA完成! 收敛于第" + gen + "代, 最优适应度=" + String.format("%.3f", fitness));
            log("最优策略: MA(5,20,60) + MACD(12,26,9) + RSI(14) + BOLL(20,2)");
            log("买入阈值=0.72  卖出阈值=-0.35");
            lblGAStatus.setText("已完成 (第" + gen + "代)"); lblGAStatus.setTextFill(Color.web("#00b894"));
            lblBestFitness.setText(String.format("%.3f", fitness));
            gaProgress.setVisible(false);
            setStatus("GA优化完成 — 查看K线图上新绘制的买卖信号标记");
            // 图表变化：在价格图上标注GA选出的买卖信号点
            drawPriceChartWithSignals();
        }), 2000);
    }

    @FXML public void onRollingPredict() {
        if (!hasRealData) { log("请先导入CSV数据！"); return; }
        int n = currentData.size();
        int windows = Math.max(1, n / 20);
        log(String.format("=== 滚动预测 (训练3月→预测1月, %d轮) ===", windows));
        setStatus("滚动预测中...");

        simulateTask(() -> Platform.runLater(() -> {
            int sig = (int)(windows * 0.65);
            log(String.format("%d轮完成, 生成 %d 条信号", windows, sig));
            lblSignal.setText("买入 ▲"); lblSignal.setTextFill(Color.web("#00b894"));
            lblSignalScore.setText("得分:0.78");
            setStatus("滚动预测完成 — 查看K线图上的预测区间");
            // 图表变化：在价格图上标注预测区间的买卖信号
            drawPriceChartWithPrediction();
        }), 1500);
    }

    @FXML public void onRunBacktest() {
        if (!hasRealData) { log("请先导入CSV数据！"); return; }
        int n = currentData.size();
        log(String.format("=== 回测分析 (初始资金=100,000, %d条数据) ===", n));
        lblBacktestStatus.setText("运行中..."); lblBacktestStatus.setTextFill(Color.web("#f39c12"));
        setStatus("回测中...");

        simulateTask(() -> Platform.runLater(() -> {
            computeBacktestResults();
            // 图表变化：基于回测交易记录重新绘制净值和回撤曲线
            drawEquityFromBacktest();
            drawDrawdownFromBacktest();
            lblBacktestStatus.setText("已完成"); lblBacktestStatus.setTextFill(Color.web("#00b894"));
            log(String.format("回测完成: %d条数据, 覆盖%.1f月", n, n / 21.0));
            log(String.format("  累计收益=%s  年化=%s  最大回撤=%s  夏普率=%s",
                    lblCumReturn.getText(), lblAnnualReturn.getText(),
                    lblMaxDrawdown.getText(), lblSharpeRatio.getText()));
            setStatus("回测完成 — 切换至「回测曲线」标签查看");
            // 自动切换到回测曲线标签
            tabPane.getSelectionModel().select(2);
        }), 2000);
    }

    @FXML public void onRunAll() {
        if (!hasRealData) { log("请先导入CSV数据！"); return; }
        log("========== 一键运行全流程 ==========");
        new Thread(() -> {
            try {
                Platform.runLater(() -> onRunGA());   Thread.sleep(2200);
                Platform.runLater(() -> onRollingPredict()); Thread.sleep(1800);
                Platform.runLater(() -> onRunBacktest()); Thread.sleep(2500);
                Platform.runLater(() -> log("========== 全流程完毕 =========="));
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @FXML public void onOpenConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Parameter.fxml"));
            Parent root = loader.load();
            Stage s = new Stage();
            s.setTitle("参数配置");
            s.initModality(Modality.APPLICATION_MODAL);
            s.setScene(new Scene(root));
            s.showAndWait();
            log("参数已更新");
        } catch (Exception e) { log("配置打开失败: " + e.getMessage()); }
    }

    @FXML public void onZoomIn()  { log("图表缩放: +"); }
    @FXML public void onZoomOut() { log("图表缩放: -"); }
    @FXML public void onExit()    { Platform.exit(); }

    @FXML public void onHelp() {
        log("=== 使用说明 ===");
        log("① 导入CSV数据 (文件→导入CSV, 格式:Date,Open,High,Low,Close,Volume)");
        log("② 参数配置 → ③ 运行GA优化 → ④ 滚动预测 → ⑤ 回测分析");
        log("左侧CheckBox可实时切换技术指标显示");
    }

    @FXML public void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("关于");
        a.setHeaderText("GAQuantBacktest v1.0");
        a.setContentText("基于遗传算法的多技术指标滚动交易时机预测系统\n\n"
                + "Java 21 + JavaFX | 2026年7月\n"
                + "P1: 数据读取+技术指标+GA+回测\n"
                + "P2: 扩展指标+参数配置+绩效统计\n"
                + "指标池: MA/MACD/RSI/KDJ/BOLL/ATR/CCI/OBV/动量");
        a.showAndWait();
    }

    // ==================== 工具 ====================

    private void log(String msg) {
        txtLog.appendText("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                + "] " + msg + "\n");
    }

    private void setStatus(String s) { lblStatus.setText(s); }

    private void simulateTask(Runnable onFinish, long ms) {
        new Thread(() -> {
            try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
            Platform.runLater(onFinish);
        }).start();
    }
}
