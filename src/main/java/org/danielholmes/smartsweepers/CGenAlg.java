package org.danielholmes.smartsweepers;

import java.util.Collections;
import java.util.Vector;

import static org.danielholmes.smartsweepers.Utils.RandFloat;
import static org.danielholmes.smartsweepers.Utils.RandInt;

public class CGenAlg {
    //this holds the entire population of chromosomes
    private Vector<SGenome> m_vecPop;

    // size of population
    private int m_iPopSize;

    //amount of weights per chromo
    private int m_iChromoLength;

    //total fitness of population
    private double m_dTotalFitness;

    //best fitness this population
    private double m_dBestFitness;

    //average fitness
    private double m_dAverageFitness;

    //worst
    private double m_dWorstFitness;

    //keeps track of the best genome
    private int m_iFittestGenome;

    //probability that a chromosomes bits will mutate.
    //Try figures around 0.05 to 0.3 ish
    private double m_dMutationRate;

    //probability of chromosomes crossing over bits
    //0.7 is pretty good
    private double m_dCrossoverRate;

    //generation counter
    private int m_cGeneration;

    public CGenAlg(int popsize, double MutRat, double CrossRat, int numweights) {
        m_iPopSize = popsize;
        m_dMutationRate = MutRat;
        m_dCrossoverRate = CrossRat;
        m_iChromoLength = numweights;
        m_dTotalFitness = 0;
        m_cGeneration = 0;
        m_iFittestGenome = 0;
        m_dBestFitness = 0;
        m_dWorstFitness = 99999999;
        m_vecPop = new Vector<>();

        //initialise population with chromosomes consisting of random
        //weights and all fitnesses set to zero
        for (int i=0; i<m_iPopSize; ++i)
        {
            m_vecPop.add(new SGenome());

            for (int j=0; j<m_iChromoLength; ++j)
            {
                m_vecPop.get(i).vecWeights.add(Utils.RandomClamped());
            }
        }
    }

    private void Crossover(
            Vector<Double> mum,
            Vector<Double> dad,
            Vector<Double> baby1,
            Vector<Double> baby2
    ) {
        //just return parents as offspring dependent on the rate
        //or if parents are the same
        if ( (RandFloat() > m_dCrossoverRate) || (mum == dad))
        {
            baby1 = mum;
            baby2 = dad;

            return;
        }

        //determine a crossover point
        int cp = RandInt(0, m_iChromoLength - 1);

        //create the offspring
        for (int i=0; i<cp; ++i)
        {
            baby1.add(mum.get(i));
            baby2.add(dad.get(i));
        }

        for (int i=cp; i<mum.size(); ++i)
        {
            baby1.add(dad.get(i));
            baby2.add(mum.get(i));
        }
    }

    private void Mutate(Vector<Double> chromo)
    {
        //traverse the chromosome and mutate each weight dependent
        //on the mutation rate
        for (int i=0; i<chromo.size(); ++i)
        {
            //do we perturb this weight?
            if (RandFloat() < m_dMutationRate)
            {
                //add or subtract a small value to the weight
                chromo.set(i, chromo.get(i) + (Utils.RandomClamped() * CParams.dMaxPerturbation));
            }
        }
    }


    private SGenome GetChromoRoulette() {
        //generate a random number between 0 & total fitness count
        double Slice = (double)(RandFloat() * m_dTotalFitness);

        //this will be set to the chosen chromosome
        SGenome TheChosenOne = null;

        //go through the chromosones adding up the fitness so far
        double FitnessSoFar = 0;

        for (int i=0; i<m_iPopSize; ++i)
        {
            FitnessSoFar += m_vecPop.get(i).dFitness;

            //if the fitness so far > random number return the chromo at
            //this point
            if (FitnessSoFar >= Slice)
            {
                TheChosenOne = m_vecPop.get(i);

                break;
            }

        }

        return TheChosenOne;
    }

    private void GrabNBest(int NBest, int NumCopies, Vector<SGenome> vecPop) { }

    private void CalculateBestWorstAvTot() {
    }

    private void Reset() {
    }

    //this runs the GA for one generation.
    public Vector<SGenome> Epoch(Vector<SGenome> old_pop) {
        //assign the given population to the classes population
        m_vecPop = old_pop;

        //reset the appropriate variables
        Reset();

        //sort the population (for scaling and elitism)
        Collections.sort(m_vecPop);

        //calculate best, worst, average and total fitness
        CalculateBestWorstAvTot();

        //create a temporary vector to store new chromosones
        Vector<SGenome> vecNewPop = new Vector<SGenome>();

        //Now to add a little elitism we shall add in some copies of the
        //fittest genomes. Make sure we add an EVEN number or the roulette
        //wheel sampling will crash
        if ((CParams.iNumCopiesElite * CParams.iNumElite % 2) == 0)
        {
            GrabNBest(CParams.iNumElite, CParams.iNumCopiesElite, vecNewPop);
        }


        //now we enter the GA loop

        //repeat until a new population is generated
        while (vecNewPop.size() < m_iPopSize)
        {
            //grab two chromosones
            SGenome mum = GetChromoRoulette();
            SGenome dad = GetChromoRoulette();

            //create some offspring via crossover
            Vector<Double> baby1 = new Vector<Double>();
            Vector<Double> baby2 = new Vector<Double>();

            Crossover(mum.vecWeights, dad.vecWeights, baby1, baby2);

            //now we mutate
            Mutate(baby1);
            Mutate(baby2);

            //now copy into vecNewPop population
            vecNewPop.add(new SGenome(baby1, 0));
            vecNewPop.add(new SGenome(baby2, 0));
        }

        //finished so assign new pop back into m_vecPop
        m_vecPop = vecNewPop;

        return m_vecPop;
    }


    public Vector<SGenome> GetChromos() {return m_vecPop;}

    public double AverageFitness() {return m_dTotalFitness / m_iPopSize;}

    public double BestFitness() {return m_dBestFitness;}
}
