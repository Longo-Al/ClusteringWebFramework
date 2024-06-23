package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.HierarchicalClusterMiner;
import data.Data;
import distance.AverageLinkdistance;
import distance.ClusterDistance;
import distance.SingleLinkDistance;

public class HierarchicalClusterMinerTest {
    private HierarchicalClusterMiner miner;
    private Data data;

    @Before
    public void setUp() {
        data = new Data();
        miner = new HierarchicalClusterMiner(3);
    }

    @Test
    public void testMineWithSingleLinkDistance() {
        ClusterDistance distance = new SingleLinkDistance();
        miner.mine(data, distance);
        assertNotNull(miner.toString());
        assertNotNull(miner.toString(data));
    }

    @Test
    public void testMineWithAverageLinkDistance() {
        ClusterDistance distance = new AverageLinkdistance();
        miner.mine(data, distance);
        assertNotNull(miner.toString());
        assertNotNull(miner.toString(data));
    }
}
