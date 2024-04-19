

class HierachicalClusterMiner {
	
	private Dendrogram dendrogram;

	
	public HierachicalClusterMiner(int depth) {
		dendrogram= new Dendrogram(depth);
	}
	
	
	public String toString() {
		return dendrogram.toString();
	}
	
	String toString(Data data) {
		return dendrogram.toString(data);
	}
	
	void mine(Data data, ClusterDistance distance){
		//to do
	}
	
}
