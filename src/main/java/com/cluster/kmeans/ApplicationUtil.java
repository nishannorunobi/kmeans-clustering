package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ApplicationUtil {
    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationProperties properties;

    public Model convertToModel(String[] values) {
        Model model = new Model();

        for (int i = 0; i < properties.variableAt.size(); i++) {
            String indexString = properties.variableAt.get(i);

            try {
                model.id = Long.valueOf(values[1]);
            } catch (Exception e) {
                System.err.println("Id parsing failed");
                System.err.println(e.getMessage());
                return null;
            }

            switch (indexString) {
                case "0":
                    try {
                        model.indexNo = Long.valueOf(values[0]);
                    } catch (Exception e) {
                        System.err.println("Index parsing failed");
                        System.err.println(e.getMessage());
                        return null;
                    }
                    break;
                case "1":
                    //already set id value;
                    break;
                case "5":
                    String date = values[5];
                    date = date.replaceAll("-", "");

                    try {
                        model.date = Long.valueOf(date);
                    } catch (Exception e) {
                        // wrongDataCount ++;
                        return null;
                    }
                    break;
                case "6":
                    String year = values[6];
                    model.year = year;
                    break;
                case "7":
                    String month = values[7];
                    model.month = month;
                    break;
                default:
                    break;
            }
        }

        return model;
    }

    public void clearOldClusters(List<List<String>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            List<String> cluster = clusters.get(i);
            cluster.clear();
        }

    }

    public void printCluster(List[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            System.out.println("Cluster " + i);
            List<String> cluster = clusters[i];
            System.out.println(cluster);
        }

    }

    public void updateCentroids(List<Double[]> centroids, List<String>[] clusters) {
        for (int cls = 0; cls < clusters.length; cls++) {
            ArrayList<String> clusterArray = (ArrayList<String>) clusters[cls];
            Double[] sum = new Double[properties.variableAt.size()];
            for (String recordId : clusterArray) {
                String record[] = dataSource.indexBasedDataMap.get(recordId + "");
                for (int i = 0; i < properties.variableAt.size(); i++) {
                    int indexString = Integer.valueOf(properties.variableAt.get(i));
                    if (sum[i] == null) {
                        sum[i] = 0D;
                    }
                    sum[i] += Double.valueOf(record[indexString]);
                }
            }

            Double[] mean = new Double[properties.variableAt.size()];
            for (int i = 0; i < properties.variableAt.size(); i++) {
                mean[i] = sum[i] / clusterArray.size();
            }
            centroids.set(cls, mean);
        }
    }

    public List<Double[]> chooseRandomCentroidsRows() {
        List<Double[]> centroids = new ArrayList<Double[]>();

        Random random = new Random();
        int numberOfVariables = properties.variableAt == null ? 1 : properties.variableAt.size();
        Set<Long> randomIndexSet = new HashSet<Long>();


        while (randomIndexSet.size() < properties.sizeOfK) {
            long randIndex = (long) (random.nextInt(dataSource.recordsIds.size()));
            if (randomIndexSet.add(randIndex)) {
                Long recordId = dataSource.recordsIds.get((int) randIndex);
                Double centroidsVal[] = new Double[numberOfVariables];
                String record[] = dataSource.indexBasedDataMap.get(recordId + "");
                for (int i = 0; i < properties.variableAt.size(); i++) {
                    int indexString = Integer.valueOf(properties.variableAt.get(i));
                    centroidsVal[i] = Double.valueOf(record[indexString]);
                }
                centroids.add(centroidsVal);
            }
        }
        return centroids;
    }

    public Map<String, Double[]> calculateDistances(List<Double[]> centroids) {
        for (Map.Entry<String, Model> entry : dataSource.modelBasedDataMap.entrySet()) {
            Model model = entry.getValue();
            String[] record = dataSource.indexBasedDataMap.get(model.id + "");
            Double[] distances = new Double[properties.sizeOfK];
            int k = 0;
            for (Double[] centroid : centroids) {
                double distance = 0;
                for (int ii = 0; ii < properties.variableAt.size(); ii++) {
                    int index = Integer.valueOf(properties.variableAt.get(ii));
                    long variable = Long.valueOf(record[index]);
                    double centroidVal = Double.valueOf(centroid[ii]);
                    distance += Math.pow(variable - centroidVal, 2);
                }

                distance = Math.sqrt(distance);
                distances[k++] = distance;
            }
            dataSource.distanceMatrix.put(model.id + "", distances);
        }
        return dataSource.distanceMatrix;
    }

    public boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
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

    public void printCentroid(List<Double[]> centroids) {
        System.out.println("################printing centroid#############");
        int i = 1;
        for (Double[] centroid : centroids){
            String str = "c"+i+" >> ";
            for (double centroidxyz : centroid){
                str += centroidxyz+",";
            }
            i++;
            System.out.println(str);
        }
        System.out.println("################printing centroid done #############");
    }
}
