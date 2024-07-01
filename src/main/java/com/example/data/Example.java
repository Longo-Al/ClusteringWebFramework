package com.example.data;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import com.example.clustering.Exceptions.NegativeNumberException;

/**
 * Classe che rappresenta un esempio di dati.
 */
public class Example implements Iterable<Double>{
    private List<Double> example;
    /**
     * Costruttore della classe Example.
     * 
     * @param length lunghezza dell'esempio
     * @throws NegativeNumberException se la lunghezza è minore o uguale a zero
     */
    public Example() {
        this.example = new LinkedList<Double>();
    }

    public Example(List<Double> point) {
        this.example = point;
    }

    // si noti che il metodo “set” è stato rimpiazzato dal metodo “add”
    void add(Double v) {
        example.add(v);
    }

    Double get(int index) {
        return example.get(index);
    }
    

    int size(){
        return this.example.size();
    }
    public double distance(Example newE) {
        double distance = calculateDistance(this.example, newE.example);
        return distance;
    }

    private double calculateDistance(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Both vectors must be of the same length");
        }
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            sum += Math.pow(a.get(i) - b.get(i), 2);
        }
        return sum;
    }

    @Override
    public String toString() {
        String str = "[";
        int i = 1;
        for (double d : example) {
            str += d;
            if(i < this.example.size())
                str += ";";
            i++;    
        }
        str += "]";
        return str;
    }

    @Override
    public Iterator<Double> iterator() {
        return example.iterator();
    }
    
}
