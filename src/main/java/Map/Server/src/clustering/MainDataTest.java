package Map.Server.src.clustering;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.JsonParseException;

import Map.Server.item_types.Example;
import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.database.DbAccess;
import Map.Server.src.database.JsonSafeConverter;
import Map.Server.src.database.Exception.DatabaseConnectionException;
import Map.Server.src.database.Pojo.Dataset;
import Map.Server.src.clustering.distance.AverageLinkDistance;
import Map.Server.src.clustering.distance.ClusterDistance;
import Map.Server.src.clustering.distance.SingleLinkDistance;

public class MainDataTest {
    
    public static void main(String[] args) {
        try {
            Connection connection = DbAccess.getConnection("0.0.0.0");
             // Carica il dataset di test
            List<ClusterableItem<Example>> basic = load_test_set();
            //Lo carico su db
            String name = "Alloraaa..";
            String description = "A simple use of the framework using Example clusterItem implementation";
            String item_type = "Example";
            String tags = "";
            Dataset ToLoad = new Dataset(name,description,item_type,basic,tags,connection);
            PreparedStatement stmt = ToLoad.generateInsertQuery(connection);
            ResultSet sqlresult;
            Dataset downloaded = null;
            try {
                stmt.executeUpdate();
            } catch (SQLException e) {
                    System.out.println("Duplicate founded. try to resolve "+e.getMessage());
                    sqlresult = Dataset.getInfobyName(connection, name);
                    sqlresult.next();
                    stmt = ToLoad.generateUpdateQuery(connection, sqlresult.getInt(1));
                     stmt.executeUpdate();
            }

            sqlresult = Dataset.getInfobyName(connection, name);
            sqlresult.next();
            sqlresult = Dataset.getbyId(connection, sqlresult.getInt(1));
            sqlresult.next();
            downloaded = Dataset.createFromResultSet(sqlresult);
            
            List<? extends ClusterableItem<?>> reloaded = JsonSafeConverter.parseBlob(downloaded.getData(), downloaded.getType());
            
            //Lo rialloco    
            final ClusterableCollection<? extends ClusterableItem<?>> reBasic = new ClusterableCollection<>(reloaded);
            System.out.println("Riallocato:");
            System.out.println(reBasic.getRawData());

            HierachicalClusterMiner<? extends ClusterableItem<?>> miner = new HierachicalClusterMiner<>(reBasic,reBasic.size());
            ClusterDistance aver = new AverageLinkDistance();
            ClusterDistance best_fit = new SingleLinkDistance();
            DbAccess.releaseConnection();
            miner.mine(aver);
            System.out.println("Average Test:");
            System.out.println(miner.toVerboseString());
            System.out.println("best_fit Test:");
            miner.mine(best_fit);
            System.out.println(miner.toVerboseString());
            System.out.println(miner.toJson());
            
        } catch (SQLException | DatabaseConnectionException e) {
            System.out.println("Qualcosa sembra non funzionare con il tuo DB;" + e.getMessage());
        } catch (JsonParseException e) {
            System.out.println("Problema con la conversione Json to obj;");
        } catch (ClassNotFoundException e) {
            System.out.println("La classe richiesta non è stata trovata o non può essere utilizzata;");
        } catch (InvalidDepthException | InvalidSizeException | InvalidClustersNumberException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private static List<ClusterableItem<Example>> load_test_set(){
        List<ClusterableItem<Example>> standard_dataset = new ArrayList<>();
        //Example 1
        Example example = new Example();
        example.add(1.0);
        example.add(2.0);
        example.add(0.0);
        standard_dataset.add(example);
        //Example 2
        example = new Example();
        example.add(0.0);
        example.add(1.0);
        example.add(-1.0);
        standard_dataset.add(example);
        //Example 3
        example = new Example();
        example.add(1.0);
        example.add(3.0);
        example.add(5.0);
        standard_dataset.add(example);
        //Example 4
        example = new Example();
        example.add(1.0);
        example.add(3.0);
        example.add(4.0);
        standard_dataset.add(example);
        //Example 5
        example = new Example();
        example.add(2.0);
        example.add(2.0);
        example.add(0.0);
        standard_dataset.add(example);

        return standard_dataset;
    }
}