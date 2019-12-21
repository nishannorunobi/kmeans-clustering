package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class DataSource {
    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    ApplicationUtil applicationUtil;

    Map<String, Model> modelBasedDataMap = new HashMap<String, Model>();
    Map<String, Double[]> distanceMatrix = new HashMap<String, Double[]>();
    Map<String, Double[]> earlyDistanceMatrix = new HashMap<String, Double[]>();
    Map<String, String[]> indexBasedDataMap = new HashMap<String, String[]>();
    List<Long> recordsIds = new ArrayList<>();

    public boolean loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(applicationProperties.dataPath))) {
            String line;
            long recordsLoaded = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                Model dataModel = applicationUtil.convertToModel(values);
                if (dataModel != null) {
                    recordsIds.add(dataModel.id);
                    modelBasedDataMap.put(dataModel.id + "", dataModel);
                    indexBasedDataMap.put(dataModel.id+"", new String[]{
                            dataModel.indexNo+"",
                            dataModel.id+"",
                            dataModel.title,
                            dataModel.publication,
                            dataModel.author,
                            dataModel.date+"",
                            dataModel.year,
                            dataModel.month,
                            dataModel.content,
                            dataModel.url
                    });
                }
                recordsLoaded++;
                if (applicationProperties.howManyRecordsToRead > 0 &&
                recordsLoaded >= applicationProperties.howManyRecordsToRead){
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Records size : "+recordsIds.size());
        return true;
    }
}
