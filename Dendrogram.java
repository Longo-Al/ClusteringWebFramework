public class Dendrogram {
    private ClusterSet tree[];

    Dendrogram(int depth){
        //to do
    }

    void setClusterSet(ClusterSet c, int level){
        //to do
    }

    ClusterSet getClusterSet(int level){
        //to do
        return new ClusterSet(0);
    }

    int getDepth(){
        return 1;
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
