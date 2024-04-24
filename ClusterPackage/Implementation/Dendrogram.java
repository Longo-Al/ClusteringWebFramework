package ClusterPackage.Implementation;

import ClusterPackage.DataSet.*;

class Dendrogram {
    private ClusterSet tree[];

    protected Dendrogram(int depth){
        tree = new ClusterSet[depth];
    }

    protected void setClusterSet(ClusterSet c, int level){
        tree[level] = c;
    }

    protected ClusterSet getClusterSet(int level){
        return tree[level];
    }

    protected int getDepth(){
        return tree.length;
    }

    public String toString(){
        String v="";
        for(int i=0; i<tree.length; i++)
            v+=("level"+i+":\n"+tree[i]+"\n");

        return v;
    }

    public String toString(Data data){
        String v="";
        for(int i=0; i<tree.length; i++)
            v+=("level"+i+":\n"+tree[i].toString(data)+"\n");

        return v;
    }
}
