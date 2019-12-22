package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ApplicationUtil {
    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationProperties properties;

    public void clearOldClusters(List<List<String>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            List<String> cluster = clusters.get(i);
            cluster.clear();
        }

    }


    public void updateCentroids(List<double[]> centroids, List<String>[] clusters) {
        for (int cls = 0; cls < clusters.length; cls++) {
            ArrayList<String> clusterArray = (ArrayList<String>) clusters[cls];

            double[] sum = new double[properties.variableAt.size()];
            for (String recordId : clusterArray) {
                Long record[] = dataSource.indexBasedDataMap.get(recordId + "");
                for (int i = 0; i < properties.variableAt.size(); i++) {
                    int indexString = Integer.valueOf(properties.variableAt.get(i));
                    sum[i] += Double.valueOf(record[indexString]);
                }
            }
            if (clusterArray.size() > 1) {
                double[] mean = new double[properties.variableAt.size()];
                for (int i = 0; i < properties.variableAt.size(); i++) {
                    mean[i] = sum[i] / clusterArray.size();
                }
                centroids.set(cls, mean);
            } else {
                centroids.set(cls, sum);
            }
        }
    }

    public List<double[]> chooseRandomCentroidsRows() {

        List<double[]> centroids = new ArrayList<double[]>();
        Random random = new Random();
        int numberOfVariables = properties.variableAt == null ? 1 : properties.variableAt.size();
        Set<Long> randomIndexSet = new HashSet<Long>();


        while (randomIndexSet.size() < properties.sizeOfK) {
            long randIndex = (long) (random.nextInt(dataSource.recordsIds.size()));
            if (randomIndexSet.add(randIndex)) {
                Long recordId = dataSource.recordsIds.get((int) randIndex);
                double centroidsVal[] = new double[numberOfVariables];
                Long record[] = dataSource.indexBasedDataMap.get(recordId + "");
                for (int i = 0; i < properties.variableAt.size(); i++) {
                    int indexString = Integer.valueOf(properties.variableAt.get(i));
                    centroidsVal[i] = Double.valueOf(record[indexString]);
                }
                centroids.add(centroidsVal);
            }
        }
        return centroids;
    }

    public Map<String, Double[]> calculateDistances(List<double[]> centroids) {
        for (Map.Entry<String, Model> entry : dataSource.modelBasedDataMap.entrySet()) {
            Model model = entry.getValue();
            Long[] record = dataSource.indexBasedDataMap.get(model.id + "");
            Double[] distances = new Double[properties.sizeOfK];
            int k = 0;
            for (double[] centroid : centroids) {
                double distance = 0;
                for (int ii = 0; ii < properties.variableAt.size(); ii++) {
                    int index = Integer.valueOf(properties.variableAt.get(ii));
                    long variable = record[index];
                    double centroidVal = centroid[ii];
                    distance += Math.pow(variable - centroidVal, 2);
                }

                distance = Math.sqrt(distance);
                distances[k++] = distance;
            }
            dataSource.distanceMatrix.put(model.id + "", distances);
        }
        return dataSource.distanceMatrix;
    }


    public long computeWordWeight(String word) {
        return computeAsciiWeight(word);
        //return computeWeightHash(word);
    }

    private long computeWeightHash(String word) {
        return word.replaceAll("\\s", "")
                .toUpperCase()
                .hashCode();
    }

    public long computeAsciiWeight(String word) {
        long weight = 0;

        char[] chars = word.replaceAll("\\s", "")
                .toUpperCase()
                .toCharArray();

        for (int i = 0; i < chars.length; i++) {
            weight += ((int) chars[i] * (chars.length - i));
        }
        return weight;
    }

    public boolean clusterEqualityCheck(List[] earlyClusters, List[] ongoingClusters) {
        boolean matched = false;
        for (int i = 0; i < earlyClusters.length; i++) {
            matched = earlyClusters[i].equals(ongoingClusters[i]);
            if (!matched) {
                break;
            }
        }
        return matched;
    }

    public void storeReport(List<double[]> centroids, List[] clusters) {
        writeInDifferentFile(clusters);
        //writeIntoSameFile(clusters);
    }

    private void writeInDifferentFile(List[] clusters) {
        int i = 1;
        for (List list : clusters) {
            String fileName = "cluster"+i+".csv";
            try {
                Path file = Paths.get(properties.outputDataPath+fileName);
                Files.write(file, list, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    private void writeIntoSameFile(List[] clusters) {
        ArrayList<String> maxSizedCluster = new ArrayList();
        for (List list : clusters) {
            if (list.size() > maxSizedCluster.size()) {
                //ArrayList clusterArr = (ArrayList) list;
                maxSizedCluster.clear();
                maxSizedCluster.addAll(list);
            }
        }

        for (int i = 0; i < maxSizedCluster.size(); i++) {
            StringBuilder csvRow = new StringBuilder("");
            for (List cluster : clusters) {
                if (cluster.size() > i) {
                    csvRow.append(cluster.get(i)).append(",");
                }
            }
            String csvRowStr = csvRow.substring(0, csvRow.length() - 2);

            String fileName = "cluster.csv";
            try {
                Path file = Paths.get(properties.outputDataPath+fileName);
                Files.write(file,
                        csvRowStr.getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }
}
