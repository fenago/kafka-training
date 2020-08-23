package example.group;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 */
public class Consumer {

    private static Scanner in;
    private static boolean stop = false;

    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Please specify 2 parameters ");
            System.exit(-1);
        }
        in = new Scanner(System.in);
        final String topicName = argv[0];
        final String groupId = argv[1];

        Consumer.GroupConsumerThread ct = new Consumer.GroupConsumerThread(topicName, groupId);
        ct.start();
        String line = "";
        while (!line.equals("exit")) {
            line = in.next();
        }
        stop = true;
        System.out.println("Stopping consumer .....");
        ct.join();

    }

    private static class GroupConsumerThread extends Thread {

        private String topicName;
        private String groupId;

        public GroupConsumerThread(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
        }

        @Override
        public void run() {
            //Configure Stream Consumer
            Properties configProperties = new Properties();

            configProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("group.id", groupId);
            final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configProperties);

            // Generate Debug message about which topic the consumer is listening to
            consumer.subscribe(Arrays.asList(topicName), new ConsumerRebalanceListener() {
                public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                    System.out.println("Stopped listening to  " + Arrays.toString(collection.toArray()));
                }

                public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                    System.out.println("Started listening to  " + Arrays.toString(collection.toArray()));
                }
            });

            //Start processing messages
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                if (stop == true) {
                    consumer.close();
                    System.exit(0);
                }
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Offset -> " + record.offset() + ", Message -> " + record.value());
                }
            }
        }
    }
}
