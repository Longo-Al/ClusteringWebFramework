package ClusterPackage.Distances;

import ClusterPackage.DataSet.Data;
import ClusterPackage.Implementation.Cluster;

public interface ClusterDistance {
		abstract double distance(Cluster c1, Cluster c2, Data d);
}