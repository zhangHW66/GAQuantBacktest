package com.gaquant.backtest;

import com.gaquant.model.StockData;
import com.gaquant.model.TradeRecord;
import java.util.List;

/** 交易执行 */
public class Trade {
    public List<TradeRecord> execute(List<String> signals, List<StockData> data,
                                      double capital, double commission, double slippage) {
        return List.of();
    }
}
