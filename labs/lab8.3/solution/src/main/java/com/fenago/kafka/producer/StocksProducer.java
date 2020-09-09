package com.fenago.kafka.producer;

import com.fenago.kafka.StockAppConstants;

import static com.fenago.kafka.producer.support.StockPriceProducerUtils.*;

public class StocksProducer {

    public static void main(final String... args) {
        startProducer(StockAppConstants.TOPIC_STOCKS);
    }
}
