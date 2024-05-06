private double calculateDistance(double[] a, double[] b) {
    if (a.length != b.length) {
        throw new InvalidSizeException("Both vectors must be of the same length");
    }

    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
        sum += Math.pow(a[i] - b[i], 2);
    }

    return sum;
}
