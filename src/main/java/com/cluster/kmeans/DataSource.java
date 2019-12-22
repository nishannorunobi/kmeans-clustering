package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataSource {
    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    ApplicationUtil applicationUtil;

    @Autowired
    ModelBuilder modelBuilder;

    public static Map<String, Model> modelBasedDataMap = new HashMap<String, Model>();
    public static Map<String, Double[]> distanceMatrix = new HashMap<String, Double[]>();
    public static Map<String, Double[]> earlyDistanceMatrix = new HashMap<String, Double[]>();
    public static Map<String, Long[]> indexBasedDataMap = new HashMap<String, Long[]>();
    public static List<Long> recordsIds = new ArrayList<>();

    @Autowired
    DataSaver dataSaver;

    public boolean loadData() {
        List<Path> filesPaths = null;
        try {
            filesPaths = Files.list(Paths.get(applicationProperties.dataPath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Path path : filesPaths) {
            try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
                String line;
                long recordsLoaded = 0;
                while ((line = br.readLine()) != null) {
                    if (!applicationProperties.skipRowList.contains(recordsLoaded)) {
                        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        updateDataMapper(values);
                        recordsLoaded++;
                        if (applicationProperties.howManyRecordsToRead > 0 &&
                                recordsLoaded >= applicationProperties.howManyRecordsToRead) {
                            break;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("Records size : " + recordsIds.size());
        return true;
    }

    private void updateDataMapper(String[] values) {
        Model dataModel = modelBuilder.convertToModel(values);
        if (dataModel != null) {
            recordsIds.add(dataModel.id);
            modelBasedDataMap.put(dataModel.id + "", dataModel);
            indexBasedDataMap.put(dataModel.id + "", new Long[]{
                    dataModel.indexNo,
                    dataModel.id,
                    0L,
                    dataModel.publication,
                    dataModel.author,
                    dataModel.date,
                    dataModel.year,
                    dataModel.month,
                    0L,
                    0L
            });
        }
    }

    public void readFilesContent() {
        try (Stream<Path> paths = Files.walk(Paths.get(applicationProperties.dataPath))) {
            paths
                    .filter(path -> path.endsWith(".csv"))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try (Stream<String> stream = Files.lines(Paths.get(path.toUri().toString()))) {
                            stream.forEach(System.out::println);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
