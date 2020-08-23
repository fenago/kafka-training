package com.cloudurable.kafka.producer;


// TODO import Partitioner HINT: import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.*;

//TODO implement Partitioner interface
public class StockPricePartitioner {

    private final Set<String> importantStocks;

    public StockPricePartitioner() {
        importantStocks = new HashSet<>();
    }

    //TODO implement configure method
    // HINT
    //    @Override ... configure(Map<String, ?> configs) {
    //     HINT   final String importantStocksStr = (String) configs.get("importantStocks");
    //     HINT   Arrays.stream(importantStocksStr.split(",")).forEach(importantStocks::add);


    //TODO implement partition method
    //HINT
    //    public int partition(final String topic,
    //                         final Object objectKey,
    //                         final byte[] keyBytes,
    //                         final Object value,
    //                         final byte[] valueBytes,
    //                         final Cluster cluster) {
    // HINT
    //        if (importantStocks.contains(key)) {
    //            return importantPartition;
    //        } else {
    //            return Math.abs(key.hashCode()) % normalPartitionCount;
    //        }


    //TODO remove Override to get rid of warning.
    //@Override
    public void close() {

    }


}