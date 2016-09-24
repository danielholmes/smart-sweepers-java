package org.danielholmes.smartsweepers;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import static org.danielholmes.smartsweepers.Utils.RandFloat;

public class CController {

    //storage for the population of genomes
    private Vector<SGenome> m_vecThePopulation;

    //and the minesweepers
    private Vector<CMinesweeper> m_vecSweepers;

    //and the mines
    private Vector<SVector2D>	   m_vecMines;

    //pointer to the GA
    private CGenAlg		         m_pGA;

    private int m_NumSweepers;

    private int m_NumMines;

    private int m_NumWeightsInNN;

    //stores the average fitness per generation for use
    //in graphing.
    private Vector<Double> m_vecAvFitness;

    //stores the best fitness per generation
    private Vector<Double> m_vecBestFitness;

    //toggles the speed at which the simulation runs
    private boolean m_bFastRender;

    //cycles per generation
    private int m_iTicks;

    //generation counter
    private int m_iGenerations;

    //window dimensions
    private int cxClient;
    private int cyClient;

    //---------------------------------------constructor---------------------
    //
    //	initilaize the sweepers, their brains and the GA factory
    //
    //-----------------------------------------------------------------------
    public CController(/*HWND hwndMain*/) {
        m_NumSweepers = CParams.iNumSweepers;
        m_pGA = null;
        m_bFastRender = false;
        m_iTicks = 0;
        m_NumMines = CParams.iNumMines;
        //m_hwndMain = hwndMain;
        m_iGenerations = 0;
        cxClient = CParams.WindowWidth;
        cyClient = CParams.WindowHeight;

        m_vecAvFitness = new Vector<>();
        m_vecBestFitness = new Vector<>();

        //let's create the mine sweepers
        m_vecSweepers = new Vector<>();
        for (int i=0; i<m_NumSweepers; ++i)
        {
            m_vecSweepers.add(new CMinesweeper());
        }

        //get the total number of weights used in the sweepers
        //NN so we can initialise the GA
        m_NumWeightsInNN = m_vecSweepers.get(0).GetNumberOfWeights();

        //initialize the Genetic Algorithm class
        m_pGA = new CGenAlg(m_NumSweepers,
                CParams.dMutationRate,
                CParams.dCrossoverRate,
                m_NumWeightsInNN);

        //Get the weights from the GA and insert into the sweepers brains
        m_vecThePopulation = m_pGA.GetChromos();

        for (int i=0; i<m_NumSweepers; i++) {
            m_vecSweepers.get(i).PutWeights(m_vecThePopulation.get(i).vecWeights);
        }

        //initialize mines in random positions within the application window
        m_vecMines = new Vector<>();
        for (int i=0; i<m_NumMines; ++i)
        {
            m_vecMines.add(new SVector2D(RandFloat() * cxClient,
                    RandFloat() * cyClient));
        }

        //fill the vertex buffers
        /*for (int i=0; i<NumSweeperVerts; ++i)
        {
            m_SweeperVB.add(sweeper.get(i));
        }

        for (int i=0; i<NumMineVerts; ++i)
        {
            m_MineVB.add(mine.get(i));
        }*/
    }

    //accessor methods
    public boolean FastRender() {return m_bFastRender;}
    public void FastRender(boolean arg){m_bFastRender = arg;}
    public void FastRenderToggle()  {m_bFastRender = !m_bFastRender;}

    //	This is the main workhorse. The entire simulation is controlled from here.
    //	The comments should explain what is going on adequately.
    public boolean Update()
    {
        //run the sweepers through CParams.iNumTicks amount of cycles. During
        //this loop each sweepers NN is constantly updated with the appropriate
        //information from its surroundings. The output from the NN is obtained
        //and the sweeper is moved. If it encounters a mine its fitness is
        //updated appropriately,
        if (m_iTicks++ < CParams.iNumTicks)
        {
            for (int i=0; i<m_NumSweepers; ++i)
            {
                //update the NN and position
                if (!m_vecSweepers.get(i).Update(m_vecMines))
                {
                    //error in processing the neural net
                    //MessageBox(m_hwndMain, "Wrong amount of NN inputs!", "Error", MB_OK);
                    throw new RuntimeException("Wrong amount of NN inputs!");

                    //return false;
                }

                //see if it's found a mine
                int GrabHit = m_vecSweepers.get(i).CheckForMine(m_vecMines,
                        CParams.dMineScale);

                if (GrabHit >= 0)
                {
                    //we have discovered a mine so increase fitness
                    m_vecSweepers.get(i).IncrementFitness();

                    //mine found so replace the mine with another at a random
                    //position
                    m_vecMines.set(GrabHit, new SVector2D(RandFloat() * cxClient,
                            RandFloat() * cyClient));
                }

                //update the chromos fitness score
                m_vecThePopulation.get(i).dFitness = m_vecSweepers.get(i).Fitness();

            }
        }

        //Another generation has been completed.

        //Time to run the GA and update the sweepers with their new NNs
        else
        {
            //update the stats to be used in our stat window
            m_vecAvFitness.add(m_pGA.AverageFitness());
            m_vecBestFitness.add(m_pGA.BestFitness());

            //increment the generation counter
            ++m_iGenerations;

            //reset cycles
            m_iTicks = 0;

            //run the GA to create a new population
            m_vecThePopulation = m_pGA.Epoch(m_vecThePopulation);

            //insert the new (hopefully)improved brains back into the sweepers
            //and reset their positions etc
            for (int i=0; i<m_NumSweepers; ++i)
            {
                m_vecSweepers.get(i).PutWeights(m_vecThePopulation.get(i).vecWeights);
                m_vecSweepers.get(i).Reset();
            }
        }

        return true;
    }

    public void Render(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.drawString("Generation: " + m_iGenerations, 5, 15);

        //do not render if running at accelerated speed
        if (!m_bFastRender)
        {
            //render the mines
            for (int i=0; i<m_NumMines; ++i)
            {
                m_vecMines.get(i);

                g.setColor(Color.GREEN);
                g.drawRect(
                        (int) (m_vecMines.get(i).x - CParams.dMineScale),
                        (int) (m_vecMines.get(i).y - CParams.dMineScale),
                        (int) (CParams.dMineScale * 2),
                        (int) (CParams.dMineScale * 2)
                );
            }

            //render the sweepers
            for (int i=0; i<m_NumSweepers; i++)
            {
                if (i == CParams.iNumElite)
                {
                    g.setColor(Color.RED);
                }
                else
                {
                    g.setColor(Color.BLACK);
                }

                CMinesweeper s = m_vecSweepers.get(i);

                AffineTransform oldTransform = g.getTransform();
                g.rotate(s.Rotation(), s.Position().x, s.Position().y);

                // Body
                g.drawRect(
                        (int) (s.Position().x - CParams.iSweeperScale),
                        (int) (s.Position().y - CParams.iSweeperScale),
                        CParams.iSweeperScale * 2,
                        CParams.iSweeperScale * 2
                );
                // Left Track
                int trackWidth = CParams.iSweeperScale / 2;
                g.drawRect(
                        (int) (s.Position().x - CParams.iSweeperScale),
                        (int) (s.Position().y - CParams.iSweeperScale),
                        trackWidth,
                        CParams.iSweeperScale * 2
                );
                // Right Track
                g.drawRect(
                        (int) (s.Position().x + CParams.iSweeperScale - trackWidth),
                        (int) (s.Position().y - CParams.iSweeperScale),
                        trackWidth,
                        CParams.iSweeperScale * 2
                );

                // Nose
                int NOSE_SIZE = CParams.iSweeperScale;
                g.drawLine(
                        (int) (s.Position().x - CParams.iSweeperScale),
                        (int) (s.Position().y + CParams.iSweeperScale),
                        (int) s.Position().x,
                        (int) (s.Position().y + CParams.iSweeperScale + NOSE_SIZE)
                );
                g.drawLine(
                        (int) s.Position().x,
                        (int) (s.Position().y + CParams.iSweeperScale + NOSE_SIZE),
                        (int) (s.Position().x + CParams.iSweeperScale),
                        (int) (s.Position().y + CParams.iSweeperScale)
                );

                g.setTransform(oldTransform);
            }

        }//end if

        else
        {
            PlotStats(g);
        }
    }
    
    //  Given a surface to draw on this function displays stats and a crude
    //  graph showing best and average fitness
    private void PlotStats(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.drawString("Best Fitness:    " + m_pGA.BestFitness(), 5, 30);
        g.drawString("Average Fitness: " + m_pGA.AverageFitness(), 5, 45);

        plotGraph(g, Color.RED, m_vecBestFitness);
        plotGraph(g, Color.BLUE, m_vecAvFitness);
    }

    private void plotGraph(Graphics2D g, Color color, Vector<Double> values)
    {
        //render the graph
        double HSlice = (float)cxClient/(m_iGenerations+1);
        double VSlice = (float)cyClient/((m_pGA.BestFitness()+1)*2);

        //plot the graph for the best fitness
        double x = 0;

        g.setColor(color);

        for (int i=1; i<values.size(); ++i)
        {
            g.drawLine(
                    (int) x,
                    (int) (cyClient - VSlice * values.get(i - 1)),
                    (int) (x + HSlice),
                    (int) (cyClient - VSlice * values.get(i))
            );

            x += HSlice;
        }
    }
}
