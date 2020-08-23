package com.cloudurable.kafka.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


// TODO implement ProducerInterceptor
public class StockProducerInterceptor {

    private final Logger logger = LoggerFactory
            .getLogger(StockProducerInterceptor.class);
    private int onSendCount;
    private int onAckCount;


    //TODO Finish the onSend method
    //@Override
    public ProducerRecord onSend(final ProducerRecord record) {
        return record;  // See hint below
    }

    // HINT
//    public ProducerRecord onSend(final ProducerRecord record) {
//        onSendCount++;
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("onSend topic=%s key=%s value=%s %d \n",
//                    record.topic(), record.key(), record.value().toString(),
//                    record.partition()
//            ));
//        } else {
//            if (onSendCount % 100 == 0) {
//                logger.info(String.format("onSend topic=%s key=%s value=%s %d \n",
//                        record.topic(), record.key(), record.value().toString(),
//                        record.partition()
//                ));
//            }
//        }
//        return record;
//    }


    //TODO Finish the onAcknowledgement method
    //@Override
    // HINT public void onAcknowledgement(final RecordMetadata metadata,
    // HINT                              final Exception exception) {
        // HINT onAckCount++;
    //            if (onAckCount % 100 == 0) {
    //                logger.info(String.format("onAck topic=%s, part=%d, offset=%d\n",
    //                        metadata.topic(), metadata.partition(), metadata.offset()
    //                ));
    //            }

//TODO implement close and configure
//
//    @Override
//    public void close() {
//    }
//
//    @Override
//    public void configure(Map<String, ?> configs) {
//    }



}