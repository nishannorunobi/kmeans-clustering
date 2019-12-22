package com.cluster.kmeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KmeansServiceImpl implements KmeansService {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    ApplicationUtil helper;

    @Autowired
    PrintService printService;

    @Autowired
    DataSource dataSource;

    public void makeCluster() {

        List[] clusters = initializeClusterList();
        List[] earlyClusters = initializeClusterList();
        List<double[]> centroids = new ArrayList<>();

        long iterationNo = 0;
        while (true) {
            System.out.println("##############Iteration No:" + ++iterationNo + "################");

            if (iterationNo == 1) {
                centroids = helper.chooseRandomCentroidsRows();
            } else {
                helper.updateCentroids(centroids, clusters);
                clusters = initializeClusterList();
            }
            printService.printCentroid(centroids);

            dataSource.earlyDistanceMatrix.clear();
            Map<String, Double[]> distanceMat = helper.calculateDistances(centroids);
            //helper.printDistanceMatrix(distanceMat);
            for (Map.Entry<String, Double[]> entry : distanceMat.entrySet()) {
                Double[] distances = entry.getValue();
                double min = Integer.MAX_VALUE;
                int c = 0;
                for (int i = 0; i < distances.length; i++) {
                    if (distances[i] < min) {
                        c = i;
                        min = distances[i];
                    }
                }
                ArrayList<String> clusterNo = (ArrayList<String>) clusters[c];
                clusterNo.add(entry.getKey());
            }
            //|printService.printCluster(clusters);
            printService.printClusterSize(clusters);

            if (abortIteration(iterationNo, earlyClusters, clusters)) {
                break;
            }

            earlyClusters = initializeClusterList();
            earlyClusters = clusters.clone();
            System.out.println("##############Iteration No:" + iterationNo + " done ################");
        }
    }

    private boolean abortIteration(long iterationNo,
                                   List[] earlyClusters,
                                   List[] ongoingClusters) {

        if (applicationProperties.noOfIteration > 0 &&
                iterationNo >= applicationProperties.noOfIteration) {
            return true;
        }

        boolean clusterEquals = helper.clusterEqualityCheck(earlyClusters, ongoingClusters);

        if (clusterEquals) {
            return true;
        }
        return false;
    }

    private List[] initializeClusterList() {
        List[] clusters = new ArrayList[applicationProperties.sizeOfK];
        for (int i = 0; i < applicationProperties.sizeOfK; i++) {
            clusters[i] = new ArrayList<>();
        }
        return clusters;
    }
}
