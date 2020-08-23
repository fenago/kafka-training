package com.cloudurable.kafka.consumer;

import static com.cloudurable.kafka.consumer.ConsumerUtil.FIRST_CLUSTER;
import static com.cloudurable.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerMain1stCluster {
    public static void main(String... args) throws Exception {
        startConsumers(FIRST_CLUSTER);
    }
}
