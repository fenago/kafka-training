package com.cloudurable.kafka.consumer;

import static com.cloudurable.kafka.consumer.ConsumerUtil.THIRD_CLUSTER;
import static com.cloudurable.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerMain3rdCluster {

    public static void main(String... args) throws Exception {
        startConsumers(THIRD_CLUSTER);
    }
}
