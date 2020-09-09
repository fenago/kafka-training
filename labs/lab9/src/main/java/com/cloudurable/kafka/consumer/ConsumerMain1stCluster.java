package com.fenago.kafka.consumer;

import static com.fenago.kafka.consumer.ConsumerUtil.FIRST_CLUSTER;
import static com.fenago.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerMain1stCluster {
    public static void main(String... args) throws Exception {
        startConsumers(FIRST_CLUSTER);
    }
}
