package Map.Server.src.clustering;

import java.io.*;

import java.util.UUID;

import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.distance.ClusterDistance;

/**
 * Classe HierachicalClusterMiner
 * modella il processo di clustering
 *
 * @author Team MAP Que Nada
 */
public class HierachicalClusterMiner<T extends ClusterableItem<?>> implements Serializable {
	private final ClusterableCollection<T> data;
	/** dendrogramma */
	private final Dendrogram dendrogram;

	/**
	 * Costruttore
	 * crea un'istanza di classe HierachicalClusterMiner con profondità depth
	 * @param depth profondità del dendrogramma
	 * @throws InvalidDepthException se la profondità è minore di 1
	 */
	public HierachicalClusterMiner(ClusterableCollection<T> data,int depth) throws InvalidDepthException {
		this.data = data;
		dendrogram= new Dendrogram(depth);
	}

	/**
	 * Metodo getDepth
	 * Restituisce la profondità del dendrogramma
	 * @return la profondità del dendrogramma
	 */
	public int getDepth() {
		return dendrogram.getDepth();
	}

	public ClusterSet getResult(){
		return this.dendrogram.getResult();
	}
	/**
	 * Metodo mine
	 * calcola il clustering del dataset data
	 * @param data dataset su cui calcolare il clustering
	 * @param distance interfaccia di calcolo distanza tra cluster
	 * @throws InvalidDepthException se la profondità del dendrogramma è minore del numero di esempi
	 * @throws InvalidSizeException se la dimensione del cluster è minore di 2
	 * @throws InvalidClustersNumberException se il numero di cluster è minore di 2
	 */
	public void mine(ClusterDistance distance) throws InvalidDepthException, InvalidSizeException, InvalidClustersNumberException {
		if (getDepth() > data.size()) {
			throw new InvalidDepthException("Numero di Esempi maggiore della profondità del dendrogramma!\n");
		}

		ClusterSet level0 = new ClusterSet();
		for (UUID uuid : data) {
			Cluster c = new Cluster();
			c.addData(uuid);
			level0.add(c);
		}

		dendrogram.put(0,level0);
		for (int i = 1; i < dendrogram.getMaxDepth(); i++) {
            ClusterSet nextlevel;
            try {
                nextlevel = dendrogram.get(i - 1).mergeClosestClusters(distance, data);
				dendrogram.put(i,nextlevel);
			} catch (InvalidSizeException | InvalidClustersNumberException e) {
				i = getDepth();
                throw e;
            }

        }

	}

	/**
	 * Metodo toString
	 * Restituisce una rappresentazione testuale del dendrogramma
	 * @return una rappresentazione testuale del dendrogramma
	 */
	public String toString() {
		return dendrogram.toString();
	}

	/**
	 * Metodo toString
	 * Restituisce una rappresentazione testuale del dendrogramma
	 * @param data dataset di esempi
	 * @throws InvalidDepthException se la profondità del dendrogramma è minore del numero di esempi
	 * @return una rappresentazione testuale del dendrogramma
	 */
	public String toVerboseString() throws InvalidDepthException {
		return dendrogram.toString(data);
	}

    public String toJson() {
        return ClusterSet.toJson(this.getResult(),this.data);
    }

	/**
	 * Metodo statico per caricare un'istanza di HierachicalClusterMiner da un file
	 * @param fileName nome del file da cui caricare l'istanza
	 * @return l'istanza caricata di HierachicalClusterMiner
	 * @throws FileNotFoundException se il file non viene trovato
	 * @throws IOException se si verifica un errore di input/output
	 * @throws ClassNotFoundException se la classe dell'oggetto serializzato non viene trovata
	 * @throws IllegalArgumentException se il nome del file è nullo o vuoto
	 */
	//	public static HierachicalClusterMiner<T> loadHierachicalClusterMiner(String fileName) throws IOException, ClassNotFoundException, IllegalArgumentException {
	//		if (fileName == null || fileName.trim().isEmpty()) {
	//			throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto");
	//		}
	//		String filePath = DIRECTORY_PATH + fileName;
	//		File file = new File(filePath);
	//		if (!file.exists()) {
	//			throw new FileNotFoundException("File non trovato: " + fileName);
	//		}
	//		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
	//			HierachicalClusterMiner<T> readFile = (HierachicalClusterMiner) ois.readObject();
	//			ois.close();
	//			return readFile;
    //	    } catch (FileNotFoundException e) {
	//			throw new FileNotFoundException("File non trovato: " + fileName);
	//		}
	//	
	//	
	//	}


	/**
	 * Metodo per salvare l'istanza corrente di HierachicalClusterMiner su un file
	 * @param fileName nome del file su cui salvare l'istanza
	 * @throws FileNotFoundException se il file non viene trovato
	 * @throws IOException se si verifica un errore di input/output
	 * @throws IllegalArgumentException se il nome del file è nullo o vuoto
	 */
	//public void salva(String fileName) throws FileNotFoundException, IOException, IllegalArgumentException {
	//	final String invalidRegex = "[<>:\"|?*]";
	//	final String validRegex = "^[\\w,\\s-]+\\.(txt|csv|json|xml|dat|bin|ser)$";
	//
	//	if (fileName == null || fileName.trim().isEmpty()) {
	//		throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto");
	//	}
	//
	//	if (fileName.matches(invalidRegex)) {
	//		throw new IOException("Il nome del file contiene caratteri non validi.");
	//	}
	//
	//	if (!fileName.matches(validRegex)) {
	//		throw new IOException("Estensione del file non valida. Assicurati che il nome del file termini con una delle seguenti estensioni: .txt, .csv, .json, .xml, .dat, .bin, .ser");
	//	}
	//
	//	fileName = fileName.replace("\\", File.separator).replace("/", File.separator);
	//
	//	File directory = new File(DIRECTORY_PATH);
	//	if (!directory.exists() && !directory.mkdirs()) {
	//		throw new IOException("Impossibile creare la directory: " + DIRECTORY_PATH);
	//	}
	//
	//	String filePath = DIRECTORY_PATH + fileName;
	//	File file = new File(filePath);
	//
	//	if (file.exists()) {
	//		throw new FileAlreadyExistsException("Il file esiste già: " + fileName);
	//	}
	//
	//	File parentDir = file.getParentFile();
	//	if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
	//		throw new IOException("Impossibile creare la directory: " + parentDir.getAbsolutePath());
	//	}
	//
	//	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
	//		oos.writeObject(this);
    //    }
	//}
	
}

