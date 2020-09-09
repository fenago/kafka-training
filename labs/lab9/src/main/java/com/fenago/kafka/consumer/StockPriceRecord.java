package com.fenago.kafka.consumer;


import com.fenago.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public class StockPriceRecord  {

    private final String topic;
    private final int partition;
    private final long offset;
    private final String name;
    private final int dollars;
    private final int cents;
    private final boolean saved;
    private final TopicPartition topicPartition;


    public StockPriceRecord(String topic, int partition, long offset,
                            String name, int dollars, int cents, boolean saved) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.name = name;
        this.dollars = dollars;
        this.cents = cents;
        this.saved = saved;
        topicPartition = new TopicPartition(topic, partition);
    }

    public StockPriceRecord(StockPriceRecord source, boolean saved) {
        this.topic = source.topic;
        this.partition = source.partition;
        this.offset = source.offset;
        this.name = source.name;
        this.dollars = source.dollars;
        this.cents = source.cents;
        this.saved = saved;
        topicPartition = new TopicPartition(topic, partition);
    }

    public StockPriceRecord(StockPrice value, boolean saved,
                            ConsumerRecord<String, StockPrice> record) {
        this.topic = record.topic();
        this.partition = record.partition();
        this.offset = record.offset();
        this.name = value.getName();
        this.dollars = value.getDollars();
        this.cents = value.getCents();
        this.saved = saved;
        topicPartition = new TopicPartition(topic, partition);
    }

    public String getTopic() {
        return topic;
    }

    public long getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public int getDollars() {
        return dollars;
    }

    public int getCents() {
        return cents;
    }

    public boolean isSaved() {
        return saved;
    }

    public TopicPartition getTopicPartition() {
        return topicPartition;
    }
}
