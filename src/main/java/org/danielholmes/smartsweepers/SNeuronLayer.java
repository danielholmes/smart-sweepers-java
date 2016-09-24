package org.danielholmes.smartsweepers;

import java.util.Vector;

public class SNeuronLayer {
    public int m_NumNeurons;

    public Vector<SNeuron> m_vecNeurons;

    public SNeuronLayer(int NumNeurons, int NumInputsPerNeuron) {
        m_NumNeurons = NumNeurons;
        for (int i = 0; i < NumNeurons; ++i) {
            m_vecNeurons.add(new SNeuron(NumInputsPerNeuron));
        }
    }
}
