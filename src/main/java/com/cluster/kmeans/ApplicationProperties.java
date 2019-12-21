package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Configuration
@ConfigurationProperties
public class ApplicationProperties {

    @PostConstruct
    public void setup() {
        if (sizeOfK <= 1) {
            sizeOfK = 2;
        }
    }

    @Value("${data-path:./src/main/resources/all-the-news/articles1_0.csv}")
    public String dataPath;

    @Value("${size-of-k:2}")
    public int sizeOfK;

    @Value("${no-of-iteration:-1}")
    long noOfIteration;

    @Value("${how-many-records:-1}")
    public int howManyRecordsToRead;

    @Value("${variable-at:6}")
    public ArrayList<String> variableAt;

    public long getNoOfIteration() {
        return noOfIteration;
    }

    public void setNoOfIteration(long noOfIteration) {
        this.noOfIteration = noOfIteration;
    }

    public int getHowManyRecordsToRead() {
        return howManyRecordsToRead;
    }

    public void setHowManyRecordsToRead(int howManyRecordsToRead) {
        this.howManyRecordsToRead = howManyRecordsToRead;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public ArrayList<String> getVariableAt() {
        return variableAt;
    }

    public void setVariableAt(ArrayList<String> variableAt) {
        this.variableAt = variableAt;
    }

    public int getSizeOfK() {
        return sizeOfK;
    }

    public void setSizeOfK(int sizeOfK) {
        this.sizeOfK = sizeOfK;
    }
}
