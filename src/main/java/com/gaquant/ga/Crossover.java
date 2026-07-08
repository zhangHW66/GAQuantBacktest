package com.gaquant.ga;

import com.gaquant.model.Chromosome;

/** 交叉算子 */
public class Crossover {
    public static Chromosome[] crossover(Chromosome parent1, Chromosome parent2, double rate) {
        return new Chromosome[]{parent1, parent2};
    }
}
