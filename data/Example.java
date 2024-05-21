package data;

import exceptions.NegativeNumberException;

public class Example {
    private double [] examples;

    public Example(int length) {
        examples = new double[length];
    }

    public void set(int index, Double v) {
        if (v < 0) {
            throw new NegativeNumberException("Example values cannot be negative: " + v);
        }
        examples[index] = v;
    }

    public Double get(int index) {
        return examples[index];
    }

    public double distance(Example newE) {
        double distance = calculateDistance(this.examples, newE.examples);
        return distance;
    }

    public String toString() {
        String str = "[";
        for (double d : examples) {
            str += d;
            str += ";";
        }
        str += "]";
        return str;
    }

    private double calculateDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Both vectors must be of the same length");
        }

        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }

        return sum;
    }
}