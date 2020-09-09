package com.fenago.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaProducerExample {

    //TODO change this to match the name of topic you created with the script.
    private final static String TOPIC = "????";

    //TODO change this to pass a comma delimited list servers (localhost:9092,localhost:9093,localhost:9094)
    private final static String BOOTSTRAP_SERVERS = "???";

    //TODO ## ***ACTION*** - EDIT KafkaProducerExample.java and define createProducer.
    private static Producer<Long, String> createProducer() {

        //TODO Create props and set ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS constant
        // This sets up the bootstrap Kafka brokers.
        //TODO Give the Kafka producer a client id.
        // HINT props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        // TODO setup ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
        // TODO setup ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
        // TODO return new KafkaProducer<>(props);
        return null;
    }


    //TODO ## ***ACTION*** - EDIT KafkaProducerExample.java and finish runProducer.
    private static void runProducer(int sendMessageCount) {

        //TODO create the producer.
        final Producer<Long, String> producer = createProducer();
        long time = System.currentTimeMillis();

        try {
            for (long index = time; index < time + sendMessageCount; index++) {

                //TODO create a new record. Use the index as the record key.
                //final ProducerRecord<Long, String> record =
                //        new ProducerRecord<>(TOPIC, index, ????);

                // TODO Send the new record

                // HINT RecordMetadata metadata = producer.send(record).get();

                long elapsedTime = System.currentTimeMillis() - time;
                //TODO Print out metadata about the record that was sent.
//                System.out.printf("sent record(key=%s value=%s) " +
//                                "meta(partition=%d, offset=%d) time=%d\n",
//                        record.key(), record.value(), metadata.partition(),
//                        metadata.offset(), elapsedTime);

            }
        } finally {
            producer.flush();
            producer.close();
        }

    }

    //TODO ## ***ACTION*** - EDIT KafkaProducerExample.java and finish main method.
    public static void main(String... args)
            throws Exception {
    }


    // HINTS
    //  |
    //  |
    //  |
    //  |
    //  |
    //  |
    //  V






    // HINT - SYNC version
//    static void runProducer(final int sendMessageCount) throws Exception {
//        final Producer<Long, String> producer = createProducer();
//        long time = System.currentTimeMillis();
//
//        try {
//            for (long index = time; index < time + sendMessageCount; index++) {
//                final ProducerRecord<Long, String> record =
//                        new ProducerRecord<>(TOPIC, index,
//                                    "Hello Mom " + index);
//
//                RecordMetadata metadata = producer.send(record).get();
//
//                long elapsedTime = System.currentTimeMillis() - time;
//                System.out.printf("sent record(key=%s value=%s) " +
//                                "meta(partition=%d, offset=%d) time=%d\n",
//                        record.key(), record.value(), metadata.partition(),
//                        metadata.offset(), elapsedTime);
//
//            }
//        } finally {
//            producer.flush();
//            producer.close();
//        }
//    }

    // HINT Async
//    static void runProducer(final int sendMessageCount) throws InterruptedException {
//        final Producer<Long, String> producer = createProducer();
//        long time = System.currentTimeMillis();
//        final CountDownLatch countDownLatch = new CountDownLatch(sendMessageCount);
//
//        try {
//            for (long index = time; index < time + sendMessageCount; index++) {
//                final ProducerRecord<Long, String> record =
//                        new ProducerRecord<>(TOPIC, index, "Hello Mom " + index);
//                producer.send(record, (metadata, exception) -> {
//                    long elapsedTime = System.currentTimeMillis() - time;
//                    if (metadata != null) {
//                        System.out.printf("sent record(key=%s value=%s) " +
//                                        "meta(partition=%d, offset=%d) time=%d\n",
//                                record.key(), record.value(), metadata.partition(),
//                                metadata.offset(), elapsedTime);
//                    } else {
//                        exception.printStackTrace();
//                    }
//                    countDownLatch.countDown();
//                });
//            }
//            countDownLatch.await(25, TimeUnit.SECONDS);
//        } finally {
//            producer.flush();
//            producer.close();
//        }
//    }


    //    //The batch.size in bytes of record size, 0 disables batching
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
//
//    //Linger how much to wait for other records before sending the batch over the network.
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 20);
//
//    // The total bytes of memory the producer can use to buffer records waiting to be sent
//    // to the Kafka broker. If records are sent faster than broker can handle than
//    // the producer blocks. Used for compression and in-flight records.
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67_108_864);
//
//    //Control how much time Producer blocks before throwing BufferExhaustedException.
//        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);

}














