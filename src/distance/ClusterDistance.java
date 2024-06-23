package distance;

import data.Data;
import clustering.Cluster;

public interface ClusterDistance {
		abstract double distance(Cluster c1, Cluster c2, Data d);
}