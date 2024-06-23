package Test.distance;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.Cluster;
import data.Data;
import distance.SingleLinkDistance;

public class SingleLinkDistanceTest {
    private SingleLinkDistance distance;
    private Data data;
    private Cluster c1;
    private Cluster c2;

    @Before
    public void setUp() {
        distance = new SingleLinkDistance();
        data = new Data();
        c1 = new Cluster();
        c2 = new Cluster();
        c1.addData(0);
        c2.addData(1);
    }

    @Test
    public void testCalculateDistance() {
        double dist = distance.distance(c1, c2, data);
        assertEquals(6.0, dist, 0.01);
    }
}