package org.danielholmes.smartsweepers;

import java.util.Vector;

public class SNeuron {
    public int m_NumInputs;

    public Vector<Double> m_vecWeight;


    public SNeuron(int NumInputs) {
        m_NumInputs = NumInputs + 1;
        //we need an additional weight for the bias hence the +1
        for (int i=0; i<NumInputs+1; ++i)
        {
            //set up the weights with an initial random value
            m_vecWeight.add(Utils.RandomClamped());
        }
    }
}
