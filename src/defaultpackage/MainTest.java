package defaultpackage;

import java.util.Scanner;

import clustering.HierachicalClusterMiner;
import data.Data;
import distance.AverageLinkdistance;
import distance.ClusterDistance;
import distance.SingleLinkDistance;

/**
 * Classe principale per eseguire i test delle funzionalità.
 */
public class MainTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Acquisizione della profondità del dendrogramma
        System.out.print("Enter the depth of the dendrogram: ");
        int k = scanner.nextInt();
        
        // Creazione dell'oggetto Data
        Data data = new Data();
        System.out.println(data);
        
        // Creazione del miner con la profondità specificata
        HierachicalClusterMiner clustering = new HierachicalClusterMiner(k);
        
        // Selezione della misura di distanza
        System.out.println("Choose the type of distance measure:");
        System.out.println("1. Single Link Distance");
        System.out.println("2. Average Link Distance");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        
        ClusterDistance distance;
        if (choice == 1) {
            distance = new SingleLinkDistance();
            System.out.println("Single link distance");
        } else {
            distance = new AverageLinkdistance();
            System.out.println("Average link distance");
        }
        
        // Calcolo della matrice di distanza e stampa
        double[][] distancematrix = data.distance();
        System.out.println("Distance matrix:\n");
        for (int i = 0; i < distancematrix.length; i++) {
            for (int j = 0; j < distancematrix[i].length; j++)
                System.out.print(distancematrix[i][j] + "\t");
            System.out.println();
        }
        
        // Esecuzione del clustering e stampa dei risultati
        clustering.mine(data, distance);
        System.out.println(clustering);
        System.out.println(clustering.toString(data));
        
        scanner.close();
    }
}
