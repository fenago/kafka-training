package com.cloudurable.kafka.producer;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.common.serialization.Serializer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StockPriceSerializer implements Serializer<StockPrice> {

    @Override
    public byte[] serialize(String topic, StockPrice stockPrice) {
        // TODO Call toJson getBytes
        // HINT return stockPrice.toJson().getBytes(StandardCharsets.UTF_8);
        return "".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}

