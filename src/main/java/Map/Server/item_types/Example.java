package Map.Server.item_types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Classe Example
 * modella le entità esempio inteso come vettore di valori reali
 *
 * @author Team MAP Que Nada
 */
public class Example extends ClusterableItem<Example>{
    /** Vettore di valori reali */
    private List<Double> example; //vettore di valori reali
    /**
     * Costruttore, crea un'istanza di classe Example
     *
     */
    public Example(){
        example = new LinkedList<>();
    }
    /**
     * metodo add
     * modifica example inserendo v in coda
     *
     * @param v valore da inserire
     */
    public void add(Double v){
        example.add(v);
    }

/**
     * Metodo toString
     * restituisce una stringa contenente i valori di example
     *
     * @return s stringa contenente i valori di example
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<Double> iterator = example.iterator();

        if (iterator.hasNext())
            s.append(iterator.next());

        while (iterator.hasNext())
            s.append(",").append(iterator.next());

        return s.toString();
    }

    @Override
    public List<Double> evaluate() {
        return this.example;
    }

}

