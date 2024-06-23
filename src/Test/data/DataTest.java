package Test.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import data.Data;
import data.Example;

public class DataTest {
    private Data data;

    @Before
    public void setUp() {
        data = new Data();
    }

    @Test
    public void testGetNumberOfExamples() {
        assertEquals(5, data.getNumberOfExamples());
    }

    @Test
    public void testGetExample() {
        Example example = data.getExample(0);
        assertNotNull(example);
    }

    @Test
    public void testDistance() {
        double[][] distanceMatrix = data.distance();
        assertEquals(5, distanceMatrix.length);
        assertEquals(5, distanceMatrix[0].length);
    }
}