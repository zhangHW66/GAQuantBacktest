package com.gaquant.indicator;

import java.util.ArrayList;
import java.util.List;

/** OBV: 收盘>昨收→累加成交量, 收盘<昨收→累减, 平→不变 */
public class OBV {
    public static List<Double> calculate(List<Double> closes, List<Long> volumes) {
        int n = closes.size();
        List<Double> result = new ArrayList<>(n);

        double obv = 0;
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                if (closes.get(i) > closes.get(i - 1))
                    obv += volumes.get(i);
                else if (closes.get(i) < closes.get(i - 1))
                    obv -= volumes.get(i);
            }
            result.add(obv);
        }
        return result;
    }
}
