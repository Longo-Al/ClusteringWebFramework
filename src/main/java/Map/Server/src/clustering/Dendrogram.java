package Map.Server.src.clustering;

import java.util.HashMap;

import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Classe Dendrogram
 * Modella un dendrogramma
 *
 * @author Team MAP Que Nada
 */
public class Dendrogram extends HashMap<Integer,ClusterSet>{
    /** array di ClusterSet */
    private final int MaxDepth;
    private boolean isComplete;

    public boolean isComplete() {
        return this.isComplete;
    }

    private void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public ClusterSet getResult(){
        if (isComplete) {
            return this.get(this.MaxDepth-1);
        }
        return null;
    }
    /**
     * Costruttore
     * @param depth profondità del dendrogramma
     */
    Dendrogram(int depth) throws InvalidDepthException {
        super();
        if (depth <= 0) {
            throw new InvalidDepthException("Profondità non valida!\n");
        }
        this.MaxDepth = depth;
        setComplete(false);;
    }

    @Override
    public ClusterSet put(Integer key, ClusterSet value) {
        ClusterSet output = null;
        if (this.size() < MaxDepth) {
            output = super.put(key, value);
            if (this.size() == MaxDepth) {
                setComplete(true);
                return value;
            }
        }
        return output;
    }
    
    /**
     * Metodo getDepth
     * Restituisce la profondità del dendrogramma
     * @return la profondità del dendrogramma
     */
    int getDepth() {
        return this.size();
    }

    public int getMaxDepth() {
        return MaxDepth;
    }
    /**
     * Metodo toString
     * Restituisce una rappresentazione testuale del dendrogramma
     * @return una rappresentazione testuale del dendrogramma
     */
    public String toString(){
        StringBuilder v= new StringBuilder();
        for(Integer k: this.keySet()){
            v.append("level").append(k.intValue()+1).append(":\n").append(this.get(k)).append("\n");
        }
        return v.toString();
    }

    /**
     * Metodo toString
     * Restituisce una rappresentazione testuale del dendrogramma
     * @param <T>
     * @param data dataset di esempi
     * @return una rappresentazione testuale del dendrogramma
     */
    public String toString(ClusterableCollection<? extends ClusterableItem<?>> data) {
        StringBuilder v= new StringBuilder();
        for(Integer k: this.keySet()){
            v.append("level").append(k.intValue()+1).append(":\n").append(this.get(k).toString(data)).append("\n");
        }
        return v.toString();
    }
    
}
