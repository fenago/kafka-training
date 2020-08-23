package com.cloudurable.kafka.consumer;

import static com.cloudurable.kafka.consumer.ConsumerUtil.SECOND_CLUSTER;
import static com.cloudurable.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerMain2ndCluster {

    public static void main(String... args) throws Exception {
        startConsumers(SECOND_CLUSTER);
    }
}
