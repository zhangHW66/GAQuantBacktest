package com.gaquant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 遗传算法种群模型
 */
public class Population {
    private List<Chromosome> individuals;
    private int generation;
    private Chromosome bestIndividual;
    private double averageFitness;

    public Population() {
        this.individuals = new ArrayList<>();
        this.generation = 0;
    }

    public List<Chromosome> getIndividuals() { return individuals; }
    public void setIndividuals(List<Chromosome> individuals) { this.individuals = individuals; }

    public int getGeneration() { return generation; }
    public void setGeneration(int generation) { this.generation = generation; }

    public Chromosome getBestIndividual() { return bestIndividual; }
    public void setBestIndividual(Chromosome bestIndividual) { this.bestIndividual = bestIndividual; }

    public double getAverageFitness() { return averageFitness; }
    public void setAverageFitness(double averageFitness) { this.averageFitness = averageFitness; }

    public int getSize() { return individuals.size(); }
}
