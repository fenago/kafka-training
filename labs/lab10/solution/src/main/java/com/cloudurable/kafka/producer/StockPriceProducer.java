package com.cloudurable.kafka.producer;

import com.cloudurable.kafka.StockAppConstants;

import static com.cloudurable.kafka.producer.support.StockPriceSerializer.StockPriceProducerUtils.*;

public class StockPriceProducer {

    public static void main(final String... args) {
        startProducer(StockAppConstants.TOPIC);
    }
}
