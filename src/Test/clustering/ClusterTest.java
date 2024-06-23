package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.Cluster;

public class ClusterTest {
    private Cluster cluster;

    @Before
    public void setUp() {
        cluster = new Cluster();
    }

    @Test
    public void testAddData() {
        cluster.addData(1);
        assertEquals(1, cluster.getSize());
        assertEquals(1, cluster.getElement(0));
    }

    @Test
    public void testCreateACopy() {
        cluster.addData(1);
        Cluster copy = cluster.createACopy();
        assertEquals(1, copy.getSize());
        assertEquals(1, copy.getElement(0));
    }

    @Test
    public void testMergeCluster() {
        Cluster anotherCluster = new Cluster();
        anotherCluster.addData(2);
        Cluster mergedCluster = cluster.mergeCluster(anotherCluster);
        assertEquals(2, mergedCluster.getSize());
        assertEquals(1, mergedCluster.getElement(0));
        assertEquals(2, mergedCluster.getElement(1));
    }
}
