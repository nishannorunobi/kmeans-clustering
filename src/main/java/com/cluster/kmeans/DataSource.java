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

    Map<String, Model> datasetHashMap = new HashMap<String, Model>();
    List<Long> recordsIds = new ArrayList<>();

    public boolean loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(applicationProperties.dataPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                //String[] values = line.split(",");
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                Model dataModel = applicationUtil.convertToModel(values);
                if (dataModel != null) {
                    recordsIds.add(dataModel.id);
                    datasetHashMap.put(dataModel.id + "", dataModel);
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
