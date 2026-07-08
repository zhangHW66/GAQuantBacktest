package com.gaquant.controller;

import com.gaquant.config.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * 参数配置控制器
 */
public class ParameterController {

    @FXML private Spinner<Integer> spnPopulationSize, spnMaxIterations;
    @FXML private Slider sldCrossoverRate, sldMutationRate;
    @FXML private Label lblCrossoverRate, lblMutationRate;

    @FXML private Spinner<Integer> spnInitCapital;
    @FXML private Spinner<Double> spnCommission, spnSlippage;

    @FXML private Spinner<Integer> spnTrainPeriod, spnTestPeriod;

    private final AppConfig config = AppConfig.getInstance();

    @FXML
    public void initialize() {
        loadConfigToUI();
        sldCrossoverRate.valueProperty().addListener((o, ov, nv) ->
                lblCrossoverRate.setText(String.format("%.2f", nv.doubleValue())));
        sldMutationRate.valueProperty().addListener((o, ov, nv) ->
                lblMutationRate.setText(String.format("%.2f", nv.doubleValue())));
    }

    private void loadConfigToUI() {
        spnPopulationSize.getValueFactory().setValue(config.getPopulationSize());
        spnMaxIterations.getValueFactory().setValue(config.getMaxIterations());
        sldCrossoverRate.setValue(config.getCrossoverRate());
        sldMutationRate.setValue(config.getMutationRate());

        spnInitCapital.getValueFactory().setValue((int) config.getInitCapital());
        spnCommission.getValueFactory().setValue(config.getCommission() * 100);
        spnSlippage.getValueFactory().setValue(config.getSlippage() * 100);

        spnTrainPeriod.getValueFactory().setValue(config.getTrainPeriod());
        spnTestPeriod.getValueFactory().setValue(config.getTestPeriod());
    }

    @FXML
    public void onSave() {
        config.setPopulationSize(spnPopulationSize.getValue());
        config.setMaxIterations(spnMaxIterations.getValue());
        config.setCrossoverRate(sldCrossoverRate.getValue());
        config.setMutationRate(sldMutationRate.getValue());

        config.setInitCapital(spnInitCapital.getValue());
        config.setCommission(spnCommission.getValue() / 100.0);
        config.setSlippage(spnSlippage.getValue() / 100.0);

        config.setTrainPeriod(spnTrainPeriod.getValue());
        config.setTestPeriod(spnTestPeriod.getValue());

        Alert a = new Alert(Alert.AlertType.INFORMATION, "配置已保存", ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();

        // 关闭窗口
        if (spnPopulationSize.getScene() != null)
            spnPopulationSize.getScene().getWindow().hide();
    }

    @FXML
    public void onResetDefault() {
        spnPopulationSize.getValueFactory().setValue(100);
        spnMaxIterations.getValueFactory().setValue(50);
        sldCrossoverRate.setValue(0.80);
        sldMutationRate.setValue(0.10);
        spnInitCapital.getValueFactory().setValue(100000);
        spnCommission.getValueFactory().setValue(0.03);
        spnSlippage.getValueFactory().setValue(0.01);
        spnTrainPeriod.getValueFactory().setValue(3);
        spnTestPeriod.getValueFactory().setValue(1);
    }
}
