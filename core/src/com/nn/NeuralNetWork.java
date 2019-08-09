package com.nn;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Weight;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class NeuralNetWork {
    String _pathDataSet;
    String _pathNetwork;
    NeuralNetwork nnetwork;

    public NeuralNetWork(String pathDataSet, String pathNetwork){
        _pathDataSet = pathDataSet;
        _pathNetwork = pathNetwork;
    }

    public void CreateMLP(int[] neuronsInLayers){
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.LINEAR, neuronsInLayers);
        myMlPerceptron.setLabel("NeuralNetWork");
        myMlPerceptron.save(_pathNetwork);
    }

    public void LoadMLP(){
        nnetwork = NeuralNetwork.createFromFile(_pathNetwork);
    }

    public void SaveMLP(){
        nnetwork.save(_pathNetwork);
    }

    public void setWeights(double[] weights){
        nnetwork.setWeights(weights);
    }

    public double[] getWeights(){
        int total = nnetwork.getWeights().length;
        int k= 0;
        double[] w = new double[total];

        for(int h=1; h<nnetwork.getLayers().size(); h++) {
            for (int i = 0; i < nnetwork.getLayerAt(h).getNeurons().size(); i++) {
                //System.out.println("L:" + h + " N:" + i + " [ " + nnetwork.getLayerAt(h).getNeurons().get(i).getOutput() + " ] ");
                for (int j = 0; j < nnetwork.getLayerAt(h).getNeurons().get(i).getWeights().length; j++) {
                    //System.out.print(nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j] + " ");
                    Object o = nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j];
                    w[k] = (double) ((Weight) o).getValue();
                    k++;
                }
            }
        }

        return w;
    }

    public double[] TestNN(double[] input){
        nnetwork.setInput(input);
        nnetwork.calculate();

        double[] networkOutput = new double[input.length];
        networkOutput = nnetwork.getOutput();

        return networkOutput;
    }
}
