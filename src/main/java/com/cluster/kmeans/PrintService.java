package com.cluster.kmeans;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PrintService {


    public void printDataRecords() {
        System.out.println(DataSource.modelBasedDataMap);
    }

    public void printCluster(List[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            System.out.println("Cluster " + i);
            List<String> cluster = clusters[i];
            System.out.println(cluster);
        }
    }

    public void printClusterSize(List[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            List<String> cluster = clusters[i];
            System.out.println("Cluster size " + cluster.size());
        }

    }

    public void printDistanceMatrix(Map<String, Double[]> distanceMat) {
        for (Map.Entry<String, Double[]> entry : distanceMat.entrySet()) {
            Double[] distances = entry.getValue();

            String str = "";
            for (Double distance : distances) {
                str += distance + " ,";
            }
            System.out.println(entry.getKey() + " >> " + str);
        }
    }

    public void printCentroid(List<double[]> centroids) {
        System.out.println("################printing centroid#############");
        int i = 1;
        for (double[] centroid : centroids) {
            String str = "c" + i + " >> ";
            for (double centroidxyz : centroid) {
                str += centroidxyz + ",";
            }
            i++;
            System.out.println(str);
        }
        System.out.println("################printing centroid done #############");
    }
}
