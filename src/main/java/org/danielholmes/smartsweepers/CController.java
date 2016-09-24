package org.danielholmes.smartsweepers;

import java.util.Vector;

import static org.danielholmes.smartsweepers.Utils.RandFloat;

public class CController {


    /*//these hold the geometry of the sweepers and the mines
    private static int NumSweeperVerts = 16;
    const SPoint sweeper[NumSweeperVerts] = {SPoint(-1, -1),
            SPoint(-1, 1),
            SPoint(-0.5, 1),
            SPoint(-0.5, -1),

            SPoint(0.5, -1),
            SPoint(1, -1),
            SPoint(1, 1),
            SPoint(0.5, 1),

            SPoint(-0.5, -0.5),
            SPoint(0.5, -0.5),

            SPoint(-0.5, 0.5),
            SPoint(-0.25, 0.5),
            SPoint(-0.25, 1.75),
            SPoint(0.25, 1.75),
            SPoint(0.25, 0.5),
            SPoint(0.5, 0.5)};

    const int NumMineVerts = 4;
    const SPoint mine[NumMineVerts] = {SPoint(-1, -1),
            SPoint(-1, 1),
            SPoint(1, 1),
            SPoint(1, -1)};*/

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

    //vertex buffer for the sweeper shape's vertices
    private Vector<SPoint> m_SweeperVB;

    //vertex buffer for the mine shape's vertices
    private Vector<SPoint> m_MineVB;

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

        for (int i=0; i<m_NumSweepers; i++)

            m_vecSweepers.get(i).PutWeights(m_vecThePopulation.get(i).vecWeights);

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


    //	sets up the translation matrices for the mines and applies the
    //	world transform to each vertex in the vertex buffer passed to this
    //	method.
    /*void CController::WorldTransform(vector<SPoint> &VBuffer, SVector2D vPos)
    {
        //create the world transformation matrix
        C2DMatrix matTransform;

        //scale
        matTransform.Scale(CParams.dMineScale, CParams.dMineScale);

        //translate
        matTransform.Translate(vPos.x, vPos.y);

        //transform the ships vertices
        matTransform.TransformSPoints(VBuffer);
    }*/


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

    /*
    public void Render(HDC surface)
    {
        //render the stats
        string s = "Generation:          " + itos(m_iGenerations);
        TextOut(surface, 5, 0, s.c_str(), s.size());

        //do not render if running at accelerated speed
        if (!m_bFastRender)
        {
            //keep a record of the old pen
            m_OldPen = (HPEN)SelectObject(surface, m_GreenPen);

            //render the mines
            for (int i=0; i<m_NumMines; ++i)
            {
                //grab the vertices for the mine shape
                vector<SPoint> mineVB = m_MineVB;

                WorldTransform(mineVB, m_vecMines.get(i));

                //draw the mines
                MoveToEx(surface, (int)mineVB[0].x, (int)mineVB[0].y, NULL);

                for (int vert=1; vert<mineVB.size(); ++vert)
                {
                    LineTo(surface, (int)mineVB[vert].x, (int)mineVB[vert].y);
                }

                LineTo(surface, (int)mineVB[0].x, (int)mineVB[0].y);

            }

            //we want the fittest displayed in red
            SelectObject(surface, m_RedPen);

            //render the sweepers
            for (i=0; i<m_NumSweepers; i++)
            {
                if (i == CParams.iNumElite)
                {
                    SelectObject(surface, m_OldPen);
                }

                //grab the sweeper vertices
                vector<SPoint> sweeperVB = m_SweeperVB;

                //transform the vertex buffer
                m_vecSweepers.get(i).WorldTransform(sweeperVB);

                //draw the sweeper left track
                MoveToEx(surface, (int)sweeperVB[0].x, (int)sweeperVB[0].y, NULL);

                for (int vert=1; vert<4; ++vert)
                {
                    LineTo(surface, (int)sweeperVB[vert].x, (int)sweeperVB[vert].y);
                }

                LineTo(surface, (int)sweeperVB[0].x, (int)sweeperVB[0].y);

                //draw the sweeper right track
                MoveToEx(surface, (int)sweeperVB[4].x, (int)sweeperVB[4].y, NULL);

                for (vert=5; vert<8; ++vert)
                {
                    LineTo(surface, (int)sweeperVB[vert].x, (int)sweeperVB[vert].y);
                }

                LineTo(surface, (int)sweeperVB[4].x, (int)sweeperVB[4].y);

                MoveToEx(surface, (int)sweeperVB[8].x, (int)sweeperVB[8].y, NULL);
                LineTo(surface, (int)sweeperVB[9].x, (int)sweeperVB[9].y);

                MoveToEx(surface, (int)sweeperVB[10].x, (int)sweeperVB[10].y, NULL);

                for (vert=11; vert<16; ++vert)
                {
                    LineTo(surface, (int)sweeperVB[vert].x, (int)sweeperVB[vert].y);
                }

            }

            //put the old pen back
            SelectObject(surface, m_OldPen);

        }//end if

        else
        {
            PlotStats(surface);
        }
    }*/
    
    //  Given a surface to draw on this function displays stats and a crude
    //  graph showing best and average fitness
    /*void CController::PlotStats(HDC surface)
    {
        string s = "Best Fitness:       " + ftos(m_pGA->BestFitness());
        TextOut(surface, 5, 20, s.c_str(), s.size());

        s = "Average Fitness: " + ftos(m_pGA->AverageFitness());
        TextOut(surface, 5, 40, s.c_str(), s.size());

        //render the graph
        float HSlice = (float)cxClient/(m_iGenerations+1);
        float VSlice = (float)cyClient/((m_pGA->BestFitness()+1)*2);

        //plot the graph for the best fitness
        float x = 0;

        m_OldPen = (HPEN)SelectObject(surface, m_RedPen);

        MoveToEx(surface, 0, cyClient, NULL);

        for (int i=0; i<m_vecBestFitness.size(); ++i)
        {
            LineTo(surface, x, cyClient - VSlice*m_vecBestFitness.get(i));

            x += HSlice;
        }

        //plot the graph for the average fitness
        x = 0;

        SelectObject(surface, m_BluePen);

        MoveToEx(surface, 0, cyClient, NULL);

        for (i=0; i<m_vecAvFitness.size(); ++i)
        {
            LineTo(surface, (int)x, (int)(cyClient - VSlice*m_vecAvFitness.get(i)));

            x += HSlice;
        }

        //replace the old pen
        SelectObject(surface, m_OldPen);
    }*/
}