/* Copyright (c) 2009 & onwards. MapR Tech, Inc., All rights reserved */
package exercise;

import solution.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;

public class MyProducer {

    // Set the stream and topic to publish to.
    public static String topic = "/user/user01/pump:alert";
    // Set the number of messages to send.
    public static int numMessages = 60;
    // Declare a new producer
    public static KafkaProducer producer;

    public static void main(String[] args) throws IOException {

        configureProducer();

        for (int i = 0; i < numMessages; i++) {
            // Set content of each message.
            String messageText = "Msg " + i;

            // TODO create ProducerRecord with topic and messageText 
            ProducerRecord<String, String> rec = null;

            // TODO  Send the record to the producer client library.
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
        //   props.put("bootstrap.servers", "localhost:9092");

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(props);
    }

}
