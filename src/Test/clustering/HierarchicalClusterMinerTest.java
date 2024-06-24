package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.HierachicalClusterMiner;
import data.Data;
import distance.AverageLinkdistance;
import distance.ClusterDistance;
import distance.SingleLinkDistance;

/**
 * Classe di test per HierarchicalClusterMiner.
 */
public class HierarchicalClusterMinerTest {
    private HierachicalClusterMiner miner;
    private Data data;

    /**
     * Imposta il miner di test e i dati prima dell'esecuzione di ogni test.
     */
    @Before
    public void setUp() {
        data = new Data();
        miner = new HierachicalClusterMiner(3);
    }

    /**
     * Testa il metodo mine con SingleLinkDistance.
     */
    @Test
    public void testMineWithSingleLinkDistance() {
        ClusterDistance distance = new SingleLinkDistance();
        miner.mine(data, distance);
        assertNotNull(miner.toString());
        assertNotNull(miner.toString(data));
    }

    /**
     * Testa il metodo mine con AverageLinkdistance.
     */
    @Test
    public void testMineWithAverageLinkDistance() {
        ClusterDistance distance = new AverageLinkdistance();
        miner.mine(data, distance);
        assertNotNull(miner.toString());
        assertNotNull(miner.toString(data));
    }
}
