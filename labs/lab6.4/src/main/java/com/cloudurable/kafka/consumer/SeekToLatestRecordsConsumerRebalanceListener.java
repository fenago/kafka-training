package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

// TODO implement the interface ConsumerRebalanceListener
// HINT implements ConsumerRebalanceListener
public class SeekToLatestRecordsConsumerRebalanceListener {

    private final Consumer<String, StockPrice> consumer;
    private static final Logger logger = getLogger(SimpleStockPriceConsumer.class);

    public SeekToLatestRecordsConsumerRebalanceListener(
            final Consumer<String, StockPrice> consumer) {
        this.consumer = consumer;
    }


    //TODO implement onPartitionsAssigned
    // HINT @Override
    //  HINT public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        // HINT final Map<TopicPartition, Long> maxOffsets = getMaxOffsetsFromDatabase();
        // HINT maxOffsets.entrySet().forEach(
            // TODO Call to consumer.seek to move to the partition.
            // TODO call display seek info displaySeekInfo(topicPartition, maxOffset);



    // TODO finish this method
    private void displaySeekInfo(TopicPartition topicPartition, long maxOffset) {
        // HINT logger.info(String.format("################################ " +
        //                "Moving to offset %d " +
        //                "for partition/topic-%s-part-%d\n",
        //        maxOffset,
        // TODO print out topic name and partition number
    }


    //TODO finish this method that finds the max offset per partition
    private Map<TopicPartition, Long> getMaxOffsetsFromDatabase() {
//        final List<StockPriceRecord> records = DatabaseUtilities.readDB();
//        final Map<TopicPartition, Long> maxOffsets = new HashMap<>();
//
//        records.forEach(stockPriceRecord -> {
//            final Long offset = maxOffsets.getOrDefault(stockPriceRecord.getTopicPartition(),
//                    -1L);
//            if (stockPriceRecord.getOffset() > offset) {
//                maxOffsets.put(stockPriceRecord.getTopicPartition(),
//                        stockPriceRecord.getOffset());
//            }
//        });
//        return maxOffsets;
        return null; // BROKE FIX ME
    }


    //TODO create a no op onPartitionsRevoked
    //public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {
    //
    //}

}
