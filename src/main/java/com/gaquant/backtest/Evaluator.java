package com.gaquant.backtest;

import com.gaquant.model.EvaluationResult;
import com.gaquant.model.TradeRecord;
import java.util.*;

/** 绩效评估：统计指标 + 净值曲线 + 年度汇总 */
public class Evaluator {

    public EvaluationResult evaluate(List<TradeRecord> trades, double initCapital) {
        EvaluationResult result = new EvaluationResult();
        result.setNetValueCurve(new ArrayList<>());
        result.setAnnualSummaries(new ArrayList<>());
        // TODO: 实现评估逻辑（Step 4）
        return result;
    }
}
