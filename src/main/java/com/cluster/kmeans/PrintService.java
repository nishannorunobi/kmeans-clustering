package com.cluster.kmeans;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrintService {
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
}
