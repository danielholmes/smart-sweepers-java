package org.danielholmes.smartsweepers;

import java.util.Vector;

public class CNeuralNet {
    private int m_NumInputs;
    private int m_NumOutputs;
    private int m_NumHiddenLayers;
    private int m_NeuronsPerHiddenLyr;
    private Vector<SNeuronLayer> m_vecLayers;

    public CNeuralNet()
    {
        m_NumInputs = CParams.getParam("iNumInputs");
        m_NumOutputs =	CParams.getParam("iNumOutputs");
        m_NumHiddenLayers =	CParams.getParam("iNumHidden");
        m_NeuronsPerHiddenLyr =	CParams.getParam("iNeuronsPerHiddenLayer");

        CreateNet();
    }

    /**
     * this method builds the ANN. The weights are all initially set to
     * random values -1 < w < 1
     */
    private void CreateNet()
    {
        //create the layers of the network
        if (m_NumHiddenLayers > 0)
        {
            //create first hidden layer
            m_vecLayers.add(new SNeuronLayer(m_NeuronsPerHiddenLyr, m_NumInputs));

            for (int i=0; i<m_NumHiddenLayers-1; ++i)
            {
                m_vecLayers.add(new SNeuronLayer(m_NeuronsPerHiddenLyr,
                        m_NeuronsPerHiddenLyr));
            }

            m_vecLayers.add(new SNeuronLayer(m_NumOutputs, m_NeuronsPerHiddenLyr));
        }
        else
        {
            m_vecLayers.add(new SNeuronLayer(m_NumOutputs, m_NumInputs));
        }
    }

    /**
     * returns a vector containing the weights
     */
    public Vector<Double> GetWeights()
    {
        //this will hold the weights
        Vector<Double> weights = new Vector<Double>();

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {

            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                {
                    weights.add(m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(k));
                }
            }
        }

        return weights;
    }

    /**
     * given a vector of doubles this function replaces the weights in the NN
     * with the new values
     */
    public void PutWeights(Vector<Double> weights)
    {
        int cWeight = 0;

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {

            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                {
                    m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.set(k, weights.get(cWeight++));
                }
            }
        }
    }

    /**
     * returns the total number of weights needed for the net
     */
    public int GetNumberOfWeights()
    {
        int weights = 0;

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {

            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)

                    weights++;

            }
        }

        return weights;
    }

    /**
     * given an input vector this function calculates the output vector
     */
    public Vector<Double> Update(Vector<Double> inputs)
    {
        //stores the resultant outputs from each layer
        Vector<Double> outputs = new Vector<Double>();

        int cWeight = 0;

        //first check that we have the correct amount of inputs
        if (inputs.size() != m_NumInputs)
        {
            //just return an empty vector if incorrect.
            return outputs;
        }

        //For each layer....
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {
            if ( i > 0 )
            {
                inputs = outputs;
            }

            outputs.clear();

            cWeight = 0;

            //for each neuron sum the (inputs * corresponding weights).Throw
            //the total at our sigmoid function to get the output.
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                double netinput = 0;

                int	NumInputs = m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs;

                //for each weight
                for (int k=0; k<NumInputs - 1; ++k)
                {
                    //sum the weights x inputs
                    netinput += m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(k) *
                            inputs.get(cWeight++);
                }

                //add in the bias
                netinput += m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(NumInputs-1) *
                        CParams.getParam("dBias");

                //we can store the outputs from each layer as we generate them.
                //The combined activation is first filtered through the sigmoid
                //function
                outputs.add(Sigmoid(netinput, CParams.getParam("dActivationResponse")));

                cWeight = 0;
            }
        }

        return outputs;
    }

    private double Sigmoid(double netinput, double response)
    {
        return ( 1 / ( 1 + Math.exp(-netinput / response)));
    }
}
