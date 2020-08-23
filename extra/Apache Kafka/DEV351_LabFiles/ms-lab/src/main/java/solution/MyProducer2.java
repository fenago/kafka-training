/* Copyright (c) 2009 & onwards. MapR Tech, Inc., All rights reserved */
package solution;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MyProducer2 {

    // Set the stream and topic to publish to.
    public static String topic = "/user/user01/pump:alert";

    // Set the number of messages to send.
    public static int numMessages = 60;
    // Declare a new producer
    public static KafkaProducer producer;
    public static String id = "producer";
    public static int partition = 0;
    public static String partitionmode = "n";

    public static void main(String[] args) throws IOException {
        ProducerRecord<String, String> rec;
        if (args != null && args.length > 0) {
            try {
                partition = Integer.parseInt(args[0]);
                id += args[0];
                if (args.length > 1) {
                    partitionmode = args[1];
                }
            } catch (NumberFormatException e) {
                partition = 0;
                partitionmode = "n";
            }
        }

        configureProducer();
        for (int i = 0; i < numMessages; i++) {
            // Set content of each message.
            String messageText = "Msg " + i + " from " + id;
            String key = "" + i;

            /* Add each message to a record. A ProducerRecord object
             identifies the topic or specific partition to publish
             a message to. */
            switch (partitionmode) {
                case "p":
                    // send to input partition
                    rec = new ProducerRecord<>(topic, partition, key, messageText);
                    break;
                case "k":
                    // send to key hashed partition 
                    rec = new ProducerRecord<String, String>(topic, key, messageText);
                    break;
                case "n":
                default:
                    rec = new ProducerRecord<String, String>(topic, messageText);
            }

            // Send the record to the producer client library.
            producer.send(rec,
                    new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            System.out.println(id + " sent record offset " + metadata.offset());
                        }
                    });

            System.out.println("Sent message number " + i);
        }
        producer.close();
        System.out.println("All done.");

        System.exit(1);

    }

    /* Set the value for a configuration parameter.
     This configuration parameter specifies which class
     to use to serialize the value of each message.*/
    public static void configureProducer() {
        Properties props = new Properties();
        props.put("client.id", id);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(props);
    }

}
