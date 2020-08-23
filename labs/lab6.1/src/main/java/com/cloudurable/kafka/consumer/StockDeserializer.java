package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// TODO StockDeserializer should implement Deserializer<StockPrice>
public class StockDeserializer  {


    // TODO Implement the deserialize method
    //@Override
    // HINT: public StockPrice deserialize(final String topic, final byte[] data) {
    // HINT:    return new StockPrice(new String(data, StandardCharsets.UTF_8));
    //

    //TODO Implement configure and close methods of the Deserializer interface.
//
//    @Override
//    public void configure(Map<String, ?> configs, boolean isKey) {
//    }
//
//    @Override
//    public void close() {
//    }
}
