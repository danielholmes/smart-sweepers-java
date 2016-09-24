package org.danielholmes.smartsweepers;

import java.util.Vector;

import static org.danielholmes.smartsweepers.Utils.Clamp;
import static org.danielholmes.smartsweepers.Utils.RandFloat;

public class CMinesweeper {
    //the minesweeper's neural net
    private CNeuralNet		m_ItsBrain;

    //its position in the world
    private SVector2D		m_vPosition;

    //direction sweeper is facing
    private SVector2D		m_vLookAt;

    //its rotation (surprise surprise)
    private double			m_dRotation;

    private double			m_dSpeed;

    //to store output from the ANN
    private double			m_lTrack, m_rTrack;

    //the sweeper's fitness score
    private double			m_dFitness;

    //the scale of the sweeper when drawn
    private double			m_dScale;

    //index position of closest mine
    private int         m_iClosestMine;

    public CMinesweeper() {
        m_ItsBrain = new CNeuralNet();
        m_dRotation = RandFloat() * CParams.dTwoPi;
        m_lTrack = 0.16;
        m_rTrack = 0.16;
        m_dFitness = 0;
        m_dScale = CParams.iSweeperScale;
        m_iClosestMine = 0;

        //create a random start position
        m_vPosition = new SVector2D((RandFloat() * CParams.WindowWidth),
                (RandFloat() * CParams.WindowHeight));

    }

    //	Resets the sweepers position, fitness and rotation
    public void Reset()
    {
        //reset the sweepers positions
        m_vPosition = new SVector2D((RandFloat() * CParams.WindowWidth),
                (RandFloat() * CParams.WindowHeight));

        //and the fitness
        m_dFitness = 0;

        //and the rotation
        m_dRotation = RandFloat()*CParams.dTwoPi;

        return;
    }

    //	sets up a translation matrix for the sweeper according to its
    //  scale, rotation and position. Returns the transformed vertices.
    /*void CMinesweeper::WorldTransform(vector<SPoint> &sweeper)
    {
        //create the world transformation matrix
        C2DMatrix matTransform;

        //scale
        matTransform.Scale(m_dScale, m_dScale);

        //rotate
        matTransform.Rotate(m_dRotation);

        //and translate
        matTransform.Translate(m_vPosition.x, m_vPosition.y);

        //now transform the ships vertices
        matTransform.TransformSPoints(sweeper);
    }*/

    //	First we take sensor readings and feed these into the sweepers brain.
    //
    //	The inputs are:
    //
    //	A vector to the closest mine (x, y)
    //	The sweepers 'look at' vector (x, y)
    //
    //	We receive two outputs from the brain.. lTrack & rTrack.
    //	So given a force for each track we calculate the resultant rotation
    //	and acceleration and apply to current velocity vector.
    //
    //-----------------------------------------------------------------------
    public boolean Update(Vector<SVector2D> mines)
    {

        //this will store all the inputs for the NN
        Vector<Double> inputs = new Vector<Double>();

        //get vector to closest mine
        SVector2D vClosestMine = GetClosestMine(mines);

        //normalise it
        vClosestMine.Vec2DNormalize();

        //add in vector to closest mine
        inputs.add(vClosestMine.x);
        inputs.add(vClosestMine.y);

        //add in sweepers look at vector
        inputs.add(m_vLookAt.x);
        inputs.add(m_vLookAt.y);

        //update the brain and get feedback
        Vector<Double> output = m_ItsBrain.Update(inputs);

        //make sure there were no errors in calculating the
        //output
        if (output.size() < CParams.iNumOutputs)
        {
            return false;
        }

        //assign the outputs to the sweepers left & right tracks
        m_lTrack = output.get(0);
        m_rTrack = output.get(1);

        //calculate steering forces
        double RotForce = m_lTrack - m_rTrack;

        //clamp rotation
        RotForce = Clamp(RotForce, -CParams.dMaxTurnRate, CParams.dMaxTurnRate);

        m_dRotation += RotForce;

        m_dSpeed = (m_lTrack + m_rTrack);

        //update Look At
        m_vLookAt.x = -Math.sin(m_dRotation);
        m_vLookAt.y = Math.cos(m_dRotation);

        //update position
        m_vPosition = m_vPosition.plus(m_vLookAt.times(m_dSpeed));

        //wrap around window limits
        if (m_vPosition.x > CParams.WindowWidth) m_vPosition.x = 0;
        if (m_vPosition.x < 0) m_vPosition.x = CParams.WindowWidth;
        if (m_vPosition.y > CParams.WindowHeight) m_vPosition.y = 0;
        if (m_vPosition.y < 0) m_vPosition.y = CParams.WindowHeight;

        return true;
    }

    //	returns the vector from the sweeper to the closest mine
    private SVector2D GetClosestMine(Vector<SVector2D> mines)
    {
        double			closest_so_far = 99999;

        SVector2D		vClosestObject = new SVector2D(0, 0);

        //cycle through mines to find closest
        for (int i=0; i<mines.size(); i++)
        {
            double len_to_object = mines.get(i).minus(m_vPosition).Vec2DLength();

            if (len_to_object < closest_so_far)
            {
                closest_so_far	= len_to_object;

                vClosestObject	= m_vPosition.minus(mines.get(i));

                m_iClosestMine = i;
            }
        }

        return vClosestObject;
    }

    //  this function checks for collision with its closest mine (calculated
    //  earlier and stored in m_iClosestMine)
    public int CheckForMine(Vector<SVector2D> mines, double size)
    {
        SVector2D DistToObject = m_vPosition.minus(mines.get(m_iClosestMine));

        if (DistToObject.Vec2DLength() < (size + 5))
        {
            return m_iClosestMine;
        }

        return -1;
    }

    public SVector2D Position() {return m_vPosition;}

    public void IncrementFitness(){++m_dFitness;}

    public double Fitness() {return m_dFitness;}

    public void PutWeights(Vector<Double> w){m_ItsBrain.PutWeights(w);}

    public int GetNumberOfWeights() {return m_ItsBrain.GetNumberOfWeights();}
}
