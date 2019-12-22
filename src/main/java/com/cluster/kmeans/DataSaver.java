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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class DataSaver implements Consumer<Path> {
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

    @Override
    public void accept(Path path) {
        String fileName = path.toUri().toString();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
