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
                case "3":
                    model.publication = computeWordWeight(values[3]);
                    break;
                case "4":
                    model.author = computeWordWeight(values[4]);
                    break;
                case "5":
                    String date = values[5];
                    date = date.replaceAll("-", "")
                            .replaceAll("\\s", "");

                    try {
                        model.date = Long.valueOf(date);
                    } catch (Exception e) {
                        // wrongDataCount ++;
                        return null;
                    }
                    break;
                case "6":
                    String year = values[6];
                    model.year = Long.valueOf(year);
                    ;
                    break;
                case "7":
                    String month = values[7];
                    model.month = Long.valueOf(month);
                    ;
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

    public long computeWordWeight(String word) {
        // return computeAsciiWeight(word);
        return computeWeightHash(word);
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
}
