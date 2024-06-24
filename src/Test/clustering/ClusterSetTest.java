package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.Cluster;
import clustering.ClusterSet;
import data.Data;
import distance.SingleLinkDistance;

/**
 * Classe di test per ClusterSet.
 */
public class ClusterSetTest {
    private ClusterSet clusterSet;
    private Data data;

    /**
     * Imposta i dati di test prima dell'esecuzione di ogni test.
     */
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

    /**
     * Testa il metodo add per aggiungere un nuovo cluster.
     */
    @Test
    public void testAdd() {
        Cluster newCluster = new Cluster();
        newCluster.addData(10);
        clusterSet.add(newCluster);
        assertEquals(6, clusterSet.getSize()); // assumendo che ci siano inizialmente 5 cluster dai dati
    }

    /**
     * Testa il metodo mergeClosestClusterSet per unire i cluster più vicini.
     */
    @Test
    public void testMergeClosestClusterSet() {
        SingleLinkDistance distance = new SingleLinkDistance();
        ClusterSet newClusterSet = clusterSet.mergeClosestClusterSet(distance, data);
        assertEquals(4, newClusterSet.getSize()); // uno in meno dopo l'unione
    }
}

