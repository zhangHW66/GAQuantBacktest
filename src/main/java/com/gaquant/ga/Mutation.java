package com.gaquant.ga;

import com.gaquant.config.IndicatorConfig;
import com.gaquant.model.Chromosome;
import java.util.Map;

/** 变异算子：掩码翻bit、参数从候选池重选、权重扰动、阈值高斯变异 */
public class Mutation {
    public static void mutate(Chromosome chromosome, double rate) {
        // 参数变异：从候选池随机重选
        for (String name : chromosome.getIndicatorParams().keySet()) {
            if (Math.random() < rate) {
                chromosome.getIndicatorParams().put(name, IndicatorConfig.randomParam(name));
            }
        }
    }
}
