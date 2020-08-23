package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;


// TODO Make SeekToConsumerRebalanceListener implement ConsumerRebalanceListener
public class SeekToConsumerRebalanceListener {
    private final Consumer<String, StockPrice> consumer;
    private final SeekTo seekTo; private boolean done;
    private final long location;
    private final long startTime = System.currentTimeMillis();
    public SeekToConsumerRebalanceListener(final Consumer<String, StockPrice> consumer, final SeekTo seekTo, final long location) {
        this.seekTo = seekTo;
        this.location = location;
        this.consumer = consumer;
    }

    //TODO implement onPartitionsAssigned
    // HINT: public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
//        if (done) return;
//        else if (System.currentTimeMillis() - startTime > 30_000) {
//            done = true;
//            return;
//        }
    // TODO onPartitionsAssigned: if SeekTo is END call seekToEnd
    // TODO onPartitionsAssigned: if SeekTo is START call seekToBeginning
    // TODO onPartitionsAssigned: if SeekTo is LOCATION call consumer.seek

//        switch (seekTo) {
//            case END:                   //Seek to end
//                consumer.seekToEnd(partitions);
//                break;
//            case START:                 //Seek to start
//                consumer.seekToBeginning(partitions);
//                break;
//            case LOCATION:              //Seek to a given location
//                partitions.forEach(topicPartition ->
//                        consumer.seek(topicPartition, location));
//                break;
//        }



//    @Override
//    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//
//    }

}
