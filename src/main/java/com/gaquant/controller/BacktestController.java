package com.gaquant.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * 回测结果控制器
 */
public class BacktestController {

    // ---- 绩效指标 ----
    @FXML private Label lblCumReturn;
    @FXML private Label lblAnnualReturn;
    @FXML private Label lblMaxDrawdown;
    @FXML private Label lblWinRate;
    @FXML private Label lblPLRatio;
    @FXML private Label lblSharpe;
    @FXML private Label lblTradeCount;
    @FXML private Label lblAvgHoldDays;

    // ---- 图表区 ----
    @FXML private StackPane equityChartPane;
    @FXML private StackPane returnChartPane;
    @FXML private StackPane drawdownChartPane;
    @FXML private StackPane monthlyChartPane;

    // ---- 策略详情 ----
    @FXML private javafx.scene.text.TextFlow txtStrategyIndicators;
    @FXML private Label lblStrategyBuyThreshold;
    @FXML private Label lblStrategySellThreshold;
    @FXML private javafx.scene.text.TextFlow txtStrategyWeights;
    @FXML private Label lblStrategyFitness;
    @FXML private Label lblStrategyGen;

    // ---- 交易记录 ----
    @FXML private TableView<?> tradeTable;
    @FXML private TableColumn<?, Integer> colIndex;
    @FXML private TableColumn<?, String> colBuyDate;
    @FXML private TableColumn<?, Double> colBuyPrice;
    @FXML private TableColumn<?, String> colSellDate;
    @FXML private TableColumn<?, Double> colSellPrice;
    @FXML private TableColumn<?, Integer> colHoldDays;
    @FXML private TableColumn<?, Double> colProfit;
    @FXML private TableColumn<?, Double> colProfitRate;
    @FXML private TableColumn<?, Double> colCumReturn;

    @FXML
    public void initialize() {
        loadDemoResults();
    }

    /** 加载演示数据 */
    private void loadDemoResults() {
        // 绩效指标
        lblCumReturn.setText("+68.52%");
        lblCumReturn.setTextFill(Color.web("#00b894"));
        lblAnnualReturn.setText("+18.35%");
        lblAnnualReturn.setTextFill(Color.web("#00b894"));
        lblMaxDrawdown.setText("-12.40%");
        lblMaxDrawdown.setTextFill(Color.web("#d63031"));
        lblWinRate.setText("62.50%");
        lblWinRate.setTextFill(Color.web("#00b894"));
        lblPLRatio.setText("2.31");
        lblSharpe.setText("1.85");
        lblTradeCount.setText("156");
        lblAvgHoldDays.setText("18.5");

        // 策略详情
        txtStrategyIndicators.getChildren().add(new Text("MA(5,20,60)  MACD(12,26,9)  RSI(14)  BOLL(20,2)"));
        lblStrategyBuyThreshold.setText("0.72");
        lblStrategyBuyThreshold.setTextFill(Color.web("#00b894"));
        lblStrategySellThreshold.setText("-0.35");
        lblStrategySellThreshold.setTextFill(Color.web("#d63031"));
        txtStrategyWeights.getChildren().add(new Text("MA:0.35  MACD:0.28  RSI:0.22  BOLL:0.15"));
        lblStrategyFitness.setText("2.8563");
        lblStrategyGen.setText("第 38 代");

        // 创建示例图表
        createDemoChart(equityChartPane, "净值曲线", Color.web("#00b894"));
        createDemoChart(returnChartPane, "收益曲线", Color.web("#0984e3"));
        createDemoChart(drawdownChartPane, "回撤曲线", Color.web("#d63031"));
        createDemoChart(monthlyChartPane, "月度收益", Color.web("#a29bfe"));
    }

    private void createDemoChart(StackPane pane, String title, Color color) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("交易周期");
        xAxis.setTickLabelsVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(title);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setPrefHeight(250);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        // 生成示例曲线数据
        for (int i = 0; i <= 250; i++) {
            double x = i / 250.0;
            double y;
            if (title.contains("回撤")) {
                y = -Math.sin(x * Math.PI * 3) * 0.08 - Math.abs(Math.cos(x * Math.PI * 2)) * 0.04;
            } else if (title.contains("净值")) {
                y = 1.0 + x * 0.5 + Math.sin(x * Math.PI * 2) * 0.15;
            } else if (title.contains("月度")) {
                y = Math.sin(i * 0.5) * 0.05 + Math.random() * 0.02;
            } else {
                y = Math.sin(x * Math.PI * 3) * 0.08 + x * 0.1;
            }
            series.getData().add(new XYChart.Data<>(i, y));
        }

        chart.getData().add(series);

        // 设置曲线颜色
        chart.lookup(".chart-series-line").setStyle("-fx-stroke: #" + colorToHex(color) + ";");
        chart.setStyle("-fx-background-color:#0d1b2a;");

        pane.getChildren().clear();
        pane.getChildren().add(chart);
    }

    private String colorToHex(Color color) {
        return String.format("%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @FXML
    public void onExportReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("导出");
        alert.setHeaderText(null);
        alert.setContentText("回测报告导出功能将在后续版本实现。");
        alert.showAndWait();
    }

    @FXML
    public void onBackToMain() {
        // 关闭当前窗口，返回主界面
        if (tradeTable.getScene() != null) {
            tradeTable.getScene().getWindow().hide();
        }
    }
}
