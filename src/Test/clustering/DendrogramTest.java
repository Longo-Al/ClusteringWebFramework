package Test.clustering;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import clustering.ClusterSet;
import clustering.Dendrogram;

/**
 * Classe di test per Dendrogram.
 */
public class DendrogramTest {
    private Dendrogram dendrogram;

    /**
     * Imposta il dendrogramma di test prima dell'esecuzione di ogni test.
     */
    @Before
    public void setUp() {
        dendrogram = new Dendrogram(3);
    }

    /**
     * Testa il metodo setClusterSet per impostare un cluster set nel dendrogramma.
     */
    @Test
    public void testSetClusterSet() {
        ClusterSet clusterSet = new ClusterSet(3);
        dendrogram.setClusterSet(clusterSet, 0);
        assertEquals(clusterSet, dendrogram.getClusterSet(0));
    }

    /**
     * Testa il metodo getDepth per ottenere la profondità del dendrogramma.
     */
    @Test
    public void testGetDepth() {
        assertEquals(3, dendrogram.getDepth());
    }
}
