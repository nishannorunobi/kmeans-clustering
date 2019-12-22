package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan({"com.cluster.kmeans"})
public class KmeansApplication {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    KmeansService kmeansService;

    @Autowired
    DataSource dataSource;

    @Autowired
    PrintService printService;

    public static void main(String[] args) {
        SpringApplication.run(KmeansApplication.class, args);
    }

    @PostConstruct
    public void startup() {
        //dataSource.readFilesContent();
        if (dataSource.loadData()) {
            //printService.printDataRecords();
            kmeansService.makeCluster();
        } else {
            System.err.println("Data load unsuccessful");
        }
    }

}
