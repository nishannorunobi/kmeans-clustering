package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KmeansServiceImpl implements KmeansService {
    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    ApplicationUtil helper;

    @Autowired
    DataSource dataSource;

    public List<List<Long>> getClusteredData() {
        Random random = new Random();
        int var = 2;
        int k = 2;

        List<List<Long>> clusters = new ArrayList();
        Set<Long> randomIndexSet = new HashSet<Long>();
        List<Double[]> centroids = new ArrayList<Double[]>();

        while(randomIndexSet.size() < k) {
            long randIndex = (long) (random.nextInt(dataSource.recordsIds.size()));
            if(randomIndexSet.add(randIndex)) {
                Long recordId = dataSource.recordsIds.get((int) randIndex);
                Double centroidsVal [] = new Double[var];
                Model record = dataSource.datasetHashMap.get(recordId+"");
                centroidsVal[0] = (double) record.id;
                centroidsVal[1] = (double) record.date;
                centroids.add(centroidsVal);
                // initialize cluster
                List<Long> clusterNo = new ArrayList<Long>();
                clusters.add(clusterNo);
            }
        }

        System.out.println(dataSource.recordsIds.size());

        int iterationNo = 1;
        while(true) {
            System.out.println("##############Iteration No:" + iterationNo + "################");
            Map<Long, Double[]> distanceMat = new HashMap<Long, Double[]>();
            for (Map.Entry<String, Model> entry : dataSource.datasetHashMap.entrySet()) {
                Model record = entry.getValue();
                Double[] distances = new Double[k];
                int i = 0;
                for (Double[] centroid : centroids) {
                    double distance = Math.sqrt(Math.pow(centroid[0] - record.id, 2)) +
                            Math.sqrt(Math.pow(centroid[1] - record.date, 2));
                    distances[i++] = distance;
                }
                distanceMat.put(record.id, distances);
            }

            //printDistanceMatrix(distanceMat);

            for (Map.Entry<Long, Double[]> entry : distanceMat.entrySet()) {
                Double[] distances = entry.getValue();
                double min = Integer.MAX_VALUE;
                int c = 0;
                for (int i = 0; i < distances.length; i++) {
                    if (distances[i] < min) {
                        c = i;
                        min = distances[i];
                    }
                }
                clusters.get(c).add(entry.getKey());
            }

            helper.printCluster(clusters);
            helper.updateCentroids(centroids, clusters);

            iterationNo++;
            if (iterationNo == 5) break;
            helper.clearOldClusters(clusters);
        }
        return clusters;
    }
}
