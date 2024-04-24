package ClusterPackage.Implementation;

import ClusterPackage.DataSet.*;
import ClusterPackage.Distances.*;

class ClusterSet {

	private Cluster C[];
	private int lastClusterIndex=0;
	
	protected ClusterSet(int k){
		C=new Cluster[k];
	}
	
	protected void add(Cluster c){
		for(int j=0;j<lastClusterIndex;j++)
			if(c==C[j]) // to avoid duplicates
				return;
		C[lastClusterIndex]=c;
		lastClusterIndex++;
	}
	
	protected Cluster get(int i){
		return C[i];
	}
	
	protected ClusterSet mergeClosestClusterSet(ClusterDistance distance, Data data){
		ClusterSet newLevel = new ClusterSet(this.lastClusterIndex - 1);
		
		//trovo la minima distanza tra i cluster.
		Cluster examinated_cluster;
		double min_distance = Double.MAX_VALUE;
		int C1_index=0,
			C2_index=0;

		for (int i = 0; i < lastClusterIndex; i++) {
			examinated_cluster = C[i];
			for(int j=i+1;j < lastClusterIndex; j++){
				double act_distance = distance.distance(examinated_cluster, C[j], data);
				if (act_distance < min_distance) {
					min_distance = act_distance;
					C1_index = i;
					C2_index = j;
				}
			}
		}
		//trovati i due cluster più vicini gli unisco e gli aggiungo al nuovo set.
		Cluster merged_cluster = C[C1_index].mergeCluster(C[C2_index]);
		newLevel.add(merged_cluster);
		//aggiungo i restanti cluster nel set.
		for (int i = 0; i < lastClusterIndex; i++) {
			if (i != C2_index && i != C1_index) {
				newLevel.add(C[i]);
			}
		}
		return newLevel;
	}
	
	public String toString(){
		String str="";
		for(int i=0;i<C.length;i++){
			if (C[i]!=null){
				str+="cluster"+i+":"+C[i]+"\n";
			}
		}
		return str;
	}

	public String toString(Data data){
		String str="";
		for(int i=0;i<C.length;i++){
			if (C[i]!=null){
				str+="cluster"+i+":"+C[i].toString(data)+"\n";
			}
		}
		return str;
	}

}
