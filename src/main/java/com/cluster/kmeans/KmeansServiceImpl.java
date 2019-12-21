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
    DataSource dataSource;

    public void makeCluster() {

        List[] clusters = new ArrayList[applicationProperties.sizeOfK];
        List<Double[]> centroids = new ArrayList<>();

        long iterationNo = 0;
        while (true) {
            System.out.println("##############Iteration No:" + ++iterationNo + "################");

            if (iterationNo == 1) {
                centroids = helper.chooseRandomCentroidsRows();
            } else {
                helper.updateCentroids(centroids, clusters);
                clusters = new ArrayList[applicationProperties.sizeOfK];
            }
            helper.printCentroid(centroids);

            dataSource.earlyDistanceMatrix.clear();
            Map<String, Double[]> distanceMat = helper.calculateDistances(centroids);
            helper.printDistanceMatrix(distanceMat);
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
                if (clusters[c] == null) {
                    clusters[c] = new ArrayList<String>();
                }
                ArrayList<String> clusterNo = (ArrayList<String>) clusters[c];
                clusterNo.add(entry.getKey());
            }
            helper.printCluster(clusters);

            if (applicationProperties.noOfIteration > 0 &&
                    iterationNo >= applicationProperties.noOfIteration) {
                break;
            }
            System.out.println("##############Iteration No:" + iterationNo + " done ################");
        }
    }
}
