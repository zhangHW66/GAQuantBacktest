package com.gaquant.ga;

import com.gaquant.model.Chromosome;
import com.gaquant.model.Population;
import java.util.*;

/**
 * 锦标赛选择 + 精英保留
 */
public class Selection {

    /** 锦标赛选择：返回新种群（保留前 eliteCount 个精英） */
    public static Population tournamentSelect(Population pop, int tournamentSize, int eliteCount) {
        List<Chromosome> oldList = pop.getIndividuals();
        int popSize = oldList.size();
        if (popSize == 0) return pop;

        // 按适应度降序排序
        List<Chromosome> sorted = new ArrayList<>(oldList);
        sorted.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        Population newPop = new Population();
        newPop.setGeneration(pop.getGeneration());
        List<Chromosome> newList = new ArrayList<>();

        // 精英保留
        int elites = Math.min(eliteCount, popSize);
        for (int i = 0; i < elites; i++) {
            newList.add(sorted.get(i).copy());
        }

        Random rng = new Random();
        // 锦标赛填充剩余
        while (newList.size() < popSize) {
            Chromosome best = null;
            for (int i = 0; i < tournamentSize; i++) {
                Chromosome c = oldList.get(rng.nextInt(popSize));
                if (best == null || c.getFitness() > best.getFitness()) {
                    best = c;
                }
            }
            newList.add(best.copy());
        }

        newPop.setIndividuals(newList);
        return newPop;
    }
}
