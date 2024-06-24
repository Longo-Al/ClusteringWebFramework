package Test.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import data.Data;
import data.Example;

/**
 * Classe di test per Data.
 */
public class DataTest {
    private Data data;

    /**
     * Imposta i dati di test prima dell'esecuzione di ogni test.
     */
    @Before
    public void setUp() {
        data = new Data();
    }

    /**
     * Testa il metodo getNumberOfExamples per ottenere il numero di esempi.
     */
    @Test
    public void testGetNumberOfExamples() {
        assertEquals(5, data.getNumberOfExamples());
    }

    /**
     * Testa il metodo getExample per ottenere un esempio specifico.
     */
    @Test
    public void testGetExample() {
        Example example = data.getExample(0);
        assertNotNull(example);
    }

    /**
     * Testa il metodo distance per calcolare la matrice delle distanze.
     */
    @Test
    public void testDistance() {
        double[][] distanceMatrix = data.distance();
        assertEquals(5, distanceMatrix.length);
        assertEquals(5, distanceMatrix[0].length);
    }
}
