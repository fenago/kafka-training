package com.fenago.kafka;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerExample {

    //TODO use the topic we created in the last lab.
    private final static String TOPIC = "???"; //"my-example-topic";
    //TODO Setup the Kafka bootstrap servers.
    private final static String BOOTSTRAP_SERVERS = "???";
            // HINT "localhost:9092,localhost:9093,localhost:9094";


    //## ***ACTION*** - EDIT src/main/java/com/fenago/kafka/KafkaConsumerExample.java and finish the runConsumer method.
    private static Consumer<Long, String> createConsumer() {

        //TODO create the properties used to create a Kafka consumer.
        final Properties props = new Properties();

        //TODO set the bootstrap servers to the running Kafka brokers
        // HINT ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS

        //TODO set the group ID
        // HINT ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer"

        //TODO setup the key deserializer and the value deserializers.
        // HINT ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer
        // HINT ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer


        // TODO Create the consumer using props.
        final Consumer<Long, String> consumer = null;//new KafkaConsumer<>(props);

        // TODO Subscribe to the topic.
        return consumer;
    }



    //## ***ACTION*** - EDIT src/main/java/com/fenago/kafka/KafkaConsumerExample.java and finish the runConsumer method.
    static void runConsumer() throws InterruptedException {
        final Consumer<Long, String> consumer = createConsumer();
        try {
            final int giveUp = 1000; int noRecordsCount = 0;

            while (true) {

                //TODO poll the records.
                final ConsumerRecords<Long, String> consumerRecords = null; //consumer.poll(1000);

                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }

                //TODO Printout partition count and record count.
                System.out.printf("New ConsumerRecords par count %d count %d\n",
                        -1, -1);
                //HINT consumerRecords.partitions().size(), consumerRecords.count());


                //TODO display the record information for each record like offset and partition.


                //TODO commit the offset read.
                //????
            }
        }
        finally {
            consumer.close();
        }

        System.out.println("DONE");
    }


    public static void main(String... args) throws Exception {
        runConsumer();
    }


}
