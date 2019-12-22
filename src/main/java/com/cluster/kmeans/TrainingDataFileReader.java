package com.cluster.kmeans;
@FunctionalInterface
public interface TrainingDataFileReader<T> {
    void accept(T t);
}