package Test.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import data.Example;

/**
 * Classe di test per Example.
 */
public class ExampleTest {
    private Example example;

    /**
     * Imposta un esempio di test prima dell'esecuzione di ogni test.
     */
    @Before
    public void setUp() {
        example = new Example(3);
        example.set(0, 1.0);
        example.set(1, 2.0);
        example.set(2, 3.0);
    }

    /**
     * Testa i metodi set e get di Example.
     */
    @Test
    public void testSetAndGet() {
        example.set(0, 1.0);
        assertEquals(1.0, example.get(0), 0.01);
    }

    /**
     * Testa il metodo distance per calcolare la distanza tra due esempi.
     */
    @Test
    public void testDistance() {
        Example newExample = new Example(3);
        newExample.set(0, 4.0);
        newExample.set(1, 5.0);
        newExample.set(2, 6.0);
        double distance = example.distance(newExample);
        assertEquals(27.0, distance, 0.01);
    }

    /**
     * Testa il metodo toString per ottenere la rappresentazione stringa dell'esempio.
     */
    @Test
    public void testToString() {
        String str = example.toString();
        assertEquals("[1.0;2.0;3.0;]", str);
    }
}
