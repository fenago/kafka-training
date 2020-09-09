package com.fenago.kafka.consumer;

import com.fenago.kafka.StockAppConstants;

import static com.fenago.kafka.consumer.ConsumerUtil.BROKERS;
import static com.fenago.kafka.consumer.ConsumerUtil.startConsumers;

public class ConsumerGreenMain {
    public static void main(String... args) throws Exception {
        final int delayMS = 100;
        final int workerCount = 3;
        final String clientId = "green";
        final String topic = StockAppConstants.TOPIC;

        startConsumers(BROKERS, delayMS, workerCount, clientId,
                topic);
    }
}
