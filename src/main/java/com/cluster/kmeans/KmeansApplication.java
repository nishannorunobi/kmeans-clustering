package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class KmeansApplication {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    KmeansService kmeansService;

    @Autowired
    DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(KmeansApplication.class, args);
    }

    @PostConstruct
    public void startup() {
        if (dataSource.loadData()) {
            kmeansService.getClusteredData();
        } else {
            System.err.println("Data load unsuccessful");
        }
    }

}
