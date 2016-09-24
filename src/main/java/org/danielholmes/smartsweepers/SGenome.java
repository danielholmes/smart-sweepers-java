package org.danielholmes.smartsweepers;

import java.util.Vector;

public class SGenome implements Comparable<SGenome> {
    public Vector<Double> vecWeights;
    public double dFitness;

    public SGenome() {
        vecWeights = new Vector<>();
        dFitness = 0;
    }

    public SGenome(Vector<Double> w, double f) {
        vecWeights = w;
        dFitness = f;
    }

    public int compareTo(SGenome other) {
        return Double.compare(dFitness, other.dFitness);
    }
}
