package Map.Server.src.clustering;

import java.util.UUID;
import java.util.List;
import java.util.Iterator;

import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Classe che rappresenta un oggetto clusterizzabile, 
 * capace di essere valutato su base numerica e misurare distanze tra istanze.
 * 
 * @author Alex Longo
 */
public class Clusterable implements Iterable<Double> {
    /**
     * Lista dei valori di valutazione dell'istanza.
     */
    private List<Double> valutation;

    /**
     * Istanza dell'adattatore che implementa {@link ClusterableItem}.
     */
    private final ClusterableItem<?> instance;

    /**
     * Identificatore univoco dell'oggetto.
     */
    private final UUID id;

    /**
     * Costruisce un nuovo oggetto Clusterable associato a un adattatore.
     *
     * @param adapter l'adattatore che fornisce i valori di valutazione.
     */
    public Clusterable(ClusterableItem<?> adapter) {
        this.instance = adapter;
        this.id = UUID.randomUUID();
    }

    /**
     * Restituisce l'identificatore univoco dell'istanza.
     *
     * @return UUID dell'istanza.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Restituisce l'adattatore associato all'istanza.
     *
     * @return il {@link ClusterableItem} associato.
     */
    public final ClusterableItem<?> getInstance() {
        return this.instance;
    }

    /**
     * Valuta l'istanza e inizializza la lista dei valori.
     */
    private void prepareCluster() {
        this.valutation = instance.evaluate();
    }

    /**
     * Restituisce una rappresentazione testuale dell'istanza,
     * delegata all'adattatore associato.
     *
     * @return rappresentazione testuale.
     */
    @Override
    public String toString() {
        return this.instance.toString();
    }

    /**
     * Restituisce un iteratore sui valori di valutazione.
     * Se i valori non sono ancora stati calcolati, li calcola.
     *
     * @return iteratore sui valori di valutazione.
     */
    @Override
    public Iterator<Double> iterator() {
        if (valutation == null) {
            prepareCluster();
        }
        return valutation.iterator();
    }

    /**
     * Calcola la distanza euclidea tra due istanze di {@code Clusterable}.
     *
     * @param a la prima istanza.
     * @param b la seconda istanza.
     * @return la distanza euclidea tra {@code a} e {@code b}.
     * @throws InvalidSizeException se le dimensioni delle valutazioni non coincidono.
     */
    public static double distance(Clusterable a, Clusterable b) throws InvalidSizeException {
        if (a.valutation == null) a.prepareCluster();
        if (b.valutation == null) b.prepareCluster();
        if (a.valutation.size() != b.valutation.size()) {
            throw new InvalidSizeException("Le dimensioni delle valutazioni non coincidono.");
        }
        double sum = 0.0;
        for (int i = 0; i < a.valutation.size(); i++) {
            double diff = a.valutation.get(i) - b.valutation.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
