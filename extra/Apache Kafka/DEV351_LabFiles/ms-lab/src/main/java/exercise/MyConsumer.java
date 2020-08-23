/* Copyright (c) 2009 & onwards. MapR Tech, Inc., All rights reserved */
package exercise;

import solution.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class MyConsumer {

// Set the stream and topic to read from.
    public static String topic = "/user/user01/pump:alert";

    // Declare a new consumer.
    public static KafkaConsumer consumer;

    public static void main(String[] args) throws IOException {
        configureConsumer(args);

        // TODO finish Subscribe to the topic.
        // Set the timeout interval for requests for unread messages.
        long pollTimeOut = 1000;
        long waitTime = 30 * 1000;
        long numberOfMsgsReceived = 0;
        while (waitTime > 0) {

            // TODO finish Request unread messages from the topic.
            ConsumerRecords<String, String> msg = null;
            if (msg.count() == 0) {
                System.out.println("No messages after 1 second wait.");
            } else {
                System.out.println("Read " + msg.count() + " messages");
                numberOfMsgsReceived += msg.count();

                // Iterate through returned records, extract the value
                // of each message, and print the value to standard output.
                // TODO finish
                Iterator<ConsumerRecord<String, String>> iter = null;

                while (iter.hasNext()) {
                    // TODO finish
                    ConsumerRecord<String, String> record = null;

                    System.out.println("Consuming " + record.toString());

                }
            }
            waitTime = waitTime - 1000;
        }
        consumer.close();
        System.out.println("Total number of messages received: " + numberOfMsgsReceived);
        System.out.println("All done.");

    }

    /* Set the value for a configuration parameter.
     This configuration parameter specifies which class
     to use to deserialize the value of each message.*/
    public static void configureConsumer(String[] args) {
        Properties props = new Properties();
        // cause consumers to start at beginning of topic on first read
        props.put("auto.offset.reset", "earliest");

        // consumer group for cursor tracking and topic sharing
        //    props.put("group.id", "myteam");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
    }

}
