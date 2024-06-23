package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DendrogramTest {
    private Dendrogram dendrogram;

    @Before
    public void setUp() {
        dendrogram = new Dendrogram(3);
    }

    @Test
    public void testSetClusterSet() {
        ClusterSet clusterSet = new ClusterSet(3);
        dendrogram.setClusterSet(clusterSet, 0);
        assertEquals(clusterSet, dendrogram.getClusterSet(0));
    }

    @Test
    public void testGetDepth() {
        assertEquals(3, dendrogram.getDepth());
    }
}
