

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
		ClusterSet level = new ClusterSet(data.getNumberOfExamples());
		for (int i = 0; i < data.numberOfExamples; i++) {
			Cluster c = new Cluster();
			c.addData(i);
			level.add(c);
		}
		dendrogram.setClusterSet(level, 0);
		for (int i = 1; i < dendrogram.getDepth(); i++) {
			ClusterSet prev_level = dendrogram.getClusterSet(i-1);
			dendrogram.setClusterSet(prev_level.mergeClosestClusterSet(distance, data),i);
		}
	}
	
}
