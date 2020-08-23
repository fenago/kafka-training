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

public class SeekToLatestRecordsConsumerRebalanceListener
                                            implements ConsumerRebalanceListener {

    private final Consumer<String, StockPrice> consumer;
    private static final Logger logger = getLogger(SimpleStockPriceConsumer.class);

    public SeekToLatestRecordsConsumerRebalanceListener(
                                    final Consumer<String, StockPrice> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        final Map<TopicPartition, Long> maxOffsets = getMaxOffsetsFromDatabase();
        maxOffsets.entrySet().forEach(
                entry -> partitions.forEach(topicPartition -> {
                    if (entry.getKey().equals(topicPartition)) {
                        long maxOffset = entry.getValue();

                        // Call to consumer.seek to move to the partition.
                        consumer.seek(topicPartition, maxOffset + 1);

                        displaySeekInfo(topicPartition, maxOffset);
                    }
                }));
    }

    private void displaySeekInfo(TopicPartition topicPartition, long maxOffset) {
        logger.info(String.format("################################" +
                        "Moving to offset %d " +
                        "for partition/topic-%s-part-%d\n",
                maxOffset,
                topicPartition.topic(),
                topicPartition.partition()));
    }

    private Map<TopicPartition, Long> getMaxOffsetsFromDatabase() {
        final List<StockPriceRecord> records = DatabaseUtilities.readDB();
        final Map<TopicPartition, Long> maxOffsets = new HashMap<>();

        records.forEach(stockPriceRecord -> {
            final Long offset = maxOffsets.getOrDefault(stockPriceRecord.getTopicPartition(), -1L);
            if (stockPriceRecord.getOffset() > offset) {
                maxOffsets.put(stockPriceRecord.getTopicPartition(),
                        stockPriceRecord.getOffset());
            }
        });
        return maxOffsets;
    }

    @Override
    public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {

    }

}
