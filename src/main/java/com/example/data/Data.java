package com.example.data;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe che rappresenta un insieme di esempi di dati.
 */
public class Data {
    private static final List<Example> data = new ArrayList<Example>();
   
    /**
     * Costruttore della classe Data.
     */
    public Data() {
        List<Example> trainingSet = new ArrayList<Example>();
        Example e;
        e = new Example(List.of(1.0,2.0,0.0));
		trainingSet.add(e);
		e = new Example(List.of(0.0,1.0,-1.0));
		trainingSet.add(e);
		e = new Example(List.of(1.0,3.0,5.0));
		trainingSet.add(e);
        e = new Example(List.of(1.0,3.0,4.0));
		trainingSet.add(e);
		e = new Example(List.of(2.0, 2.0 , 0.0));
		trainingSet.add(e);
        data.addAll(trainingSet);
    }

    public int getNumberOfExamples() {
        return data.size();
    }

    public Example getExample(int exampleIndex) {
        return data.get(exampleIndex);
    }

    public double[][] distance() {
        Integer length = data.size();
        double[][] distance_matrix = new double[length][length];
        for (int raw = 0; raw < getNumberOfExamples(); raw++) {
            for (int column = 0; column < getNumberOfExamples(); column++) {
                if (raw < column)
                    distance_matrix[raw][column] = data.get(raw).distance(data.get(column));
                else
                    distance_matrix[raw][column] = 0.0;
            }
        }
        return distance_matrix;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Data examples:\n");
        int i = 1;
        for (Example example : data) {
            str.append("\tExample n.").append(i).append(": \t");
            str.append(example).append("\n");
            i++;
        }
        return str.toString();
    }

    public static void main(String[] args) {
        Data trainingSet = new Data();
        System.out.println(trainingSet);
        double[][] distancematrix = trainingSet.distance();
        System.out.println("Distance matrix:\n");
        for (double[] row : distancematrix) {
            for (double value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}
