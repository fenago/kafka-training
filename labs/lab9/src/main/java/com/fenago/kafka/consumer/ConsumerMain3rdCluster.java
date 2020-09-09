package com.fenago.kafka.consumer;

import static com.fenago.kafka.consumer.ConsumerUtil.THIRD_CLUSTER;
import static com.fenago.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerMain3rdCluster {

    public static void main(String... args) throws Exception {
        startConsumers(THIRD_CLUSTER);
    }
}
