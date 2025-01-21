package Map.Server.src.clustering;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


/**
 * Classe Cluster
 * modella un cluster come la collezione delle posizioni occupate
 * dagli esempi raggruppati nel Cluster nel vettore data dell’oggetto
 * che modella il dataset su cui il clustering è calcolato(istanza di Data)
 *
 * @author Team MAP Que Nada
 */
public class Cluster implements Iterable<UUID>, Cloneable, Serializable {
	/**
	 * Set di interi che rappresenta gli indici degli esempi raggruppati nel cluster
	 */
	private Set<UUID> clusteredData =new TreeSet<>();

	/**
	 * Metodo addData
	 * aggiunge l'indice di posizione id al cluster
	 *
	 * @param id indice da aggiungere al cluster
	 */
	void addData(UUID id){
        clusteredData.add(id);
	}

	/**
	 * Metodo getSize
	 * restituisce la dimensione del cluster
	 *
	 * @return dimensione del cluster
	 */
	public int getSize() {
		return clusteredData.size();
	}

	/**
	 * Metodo iterator
	 * restituisce un iterator per scorrere gli elementi del cluster
	 *
	 * @return clusteredData.iterator() iterator per scorrere gli elementi del cluster
	 */
	public Iterator<UUID> iterator() {
		return clusteredData.iterator();
	}

	/**
	 * metodo clone
	 * crea una copia del cluster
	 *
	 * @return copia del cluster
	 */
	@Override
	public Cluster clone() throws CloneNotSupportedException {
		Cluster clone;
		try {
			clone = (Cluster) super.clone();
            //noinspection unchecked
    		clone.clusteredData = new TreeSet<>(this.clusteredData);
		} catch (CloneNotSupportedException e) {
			throw new CloneNotSupportedException("Errore nella clonazione!");
		}

		return clone;
	}

	/**
	 * Metodo mergeCluster
	 * crea un nuovo cluster che è la fusione del cluster corrente e del cluster c
	 *
	 * @param c cluster da unire al cluster corrente
	 * @return newC cluster che è la fusione del cluster corrente e del cluster c
	 */
	Cluster mergeCluster(Cluster c) {
		Cluster newC = new Cluster();
		Iterator<UUID> it1 = this.iterator();
		Iterator<UUID> it2 = c.iterator();

		while (it1.hasNext()) {
			newC.addData(it1.next());
		}
		while (it2.hasNext()) {
			newC.addData(it2.next());
		}

		return newC;
	}


	/**
	 * Metodo toString
	 * restituisce una stringa contenente gli indici degli esempi raggruppati nel cluster
	 *
	 * @return str stringa contenente gli indici degli esempi raggruppati nel cluster
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		Iterator<UUID> it = this.iterator();

		if (it.hasNext())
			str.append(it.next());

		while (it.hasNext())
			str.append(",").append(it.next());

		return str.toString();
	}

	/**
	 * Metodo toString
	 * restituisce una stringa contenente gli UUID degli esempi raggruppati nel cluster
	 * @param <T>
	 *
	 * @param data oggetto di classe Data che modella il dataset su cui il clustering è calcolato
	 * @return str stringa contenente gli esempi raggruppati nel cluster
	 */
	public String toString(ClusterableCollection<?> data) {
		StringBuilder str = new StringBuilder();

        for (UUID clusteredDatum : clusteredData)
            str.append("<[").append(data.getClusterable(clusteredDatum)).append("]>");

		return str.toString();
	}





}
