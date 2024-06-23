package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.Cluster;
import data.Data;
import distance.SingleLinkDistance;

public class ClusterSetTest {
    private ClusterSet clusterSet;
    private Data data;

    @Before
    public void setUp() {
        data = new Data();
        clusterSet = new ClusterSet(data.getNumberOfExamples());
        for (int i = 0; i < data.getNumberOfExamples(); i++) {
            Cluster c = new Cluster();
            c.addData(i);
            clusterSet.add(c);
        }
    }

    @Test
    public void testAdd() {
        Cluster newCluster = new Cluster();
        newCluster.addData(10);
        clusterSet.add(newCluster);
        assertEquals(6, clusterSet.getSize()); // assuming initial 5 clusters from Data
    }

    @Test
    public void testMergeClosestClusterSet() {
        SingleLinkDistance distance = new SingleLinkDistance();
        ClusterSet newClusterSet = clusterSet.mergeClosestClusterSet(distance, data);
        assertEquals(4, newClusterSet.getSize()); // one less after merging
    }
}

