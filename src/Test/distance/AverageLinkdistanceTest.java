package Test.distance;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.Cluster;
import data.Data;
import distance.AverageLinkdistance;

/**
 * Classe di test per AverageLinkdistance.
 */
public class AverageLinkdistanceTest {
    private AverageLinkdistance distance;
    private Data data;
    private Cluster c1;
    private Cluster c2;

    /**
     * Imposta i dati di test prima dell'esecuzione di ogni test.
     */
    @Before
    public void setUp() {
        distance = new AverageLinkdistance();
        data = new Data();
        c1 = new Cluster();
        c2 = new Cluster();
        c1.addData(0);
        c2.addData(1);
    }

    /**
     * Testa il metodo distance per calcolare la distanza media tra due cluster.
     */
    @Test
    public void testCalculateDistance() {
        double dist = distance.distance(c1, c2, data);
        assertEquals(6.0, dist, 0.01);
    }
}
