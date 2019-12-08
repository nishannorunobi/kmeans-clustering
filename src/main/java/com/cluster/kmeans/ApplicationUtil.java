package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.List;

@Component
public class ApplicationUtil {
    @Autowired
    DataSource dataSource;

    public Model convertToModel(String[] values) {
        Model model = new Model();
        try {
            model.id = Long.valueOf(values[1]);
        } catch (Exception e) {
            // wrongDataCount ++;
            return null;
        }
        //model.title = values[2];
        model.publication = values[3];
        // publications.add(model.publication);
        //	model.author = values[4];
        String date = values[5];


        date = date.replaceAll("-", "");

        try {
            model.date = Long.valueOf(date);
        } catch (Exception e) {
            // wrongDataCount ++;
            return null;
        }

        //	model.year = values[6];
        //	model.month = values[7];
        //	model.url = values[8];
        //model.content = values[9];
        return model;
    }

    public void clearOldClusters(List<List<Long>> clusters) {
        for(int i=0;i<clusters.size();i++) {
            List<Long> cluster = clusters.get(i);
            cluster.clear();
        }

    }

    public void printCluster(List<List<Long>> clusters) {
        for(int i=0;i<clusters.size();i++) {
            System.out.println("Cluster "+i);
            List<Long> cluster = clusters.get(i);
            System.out.println(cluster);
        }

    }

    public void updateCentroids(List<Double[]> centroids, List<List<Long>> clusters) {
        for(int i=0;i<centroids.size();i++) {
            List<Long> cluster = clusters.get(i);
            Double[] centroidVals = centroids.get(i);
            int totalId = 0;
            int totalDate = 0;
            for(long modelId:cluster) {
                Model model = dataSource.datasetHashMap.get(modelId+"");
                totalId +=model.id;
                totalDate += model.date;
            }
            centroidVals[0] = (double) (totalId/cluster.size());
            centroidVals[1] = (double) (totalDate/cluster.size());
        }
    }
}
