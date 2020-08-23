package example.simple;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

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
        System.out.println("Topic -> " + topicName);
        final long autoCommit = Long.parseLong(argv[1]);

        Consumer.ConsumerThread ct = new Consumer.ConsumerThread(topicName, autoCommit);
        ct.start();
        String line = "";
        while (!line.equals("exit")) {
            line = in.next();
        }
        stop = true;
        System.out.println("Stopping consumer .....");
        ct.join();

    }

    public static class ConsumerThread extends Thread {

        private String topicName;
        private long autoCommit;

        public ConsumerThread(String topicName, long autoCommit) {
            this.topicName = topicName;
            this.autoCommit = autoCommit;
        }

        @Override
        public void run() {
            try {
                Properties configProperties = new Properties();

                configProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                configProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                configProperties.put("group.id", "simple");
            //  configProperties.put("auto.offset.reset", "earliest");

                if (autoCommit == 1) {
                    System.out.println("Turning auto commit on");
                    configProperties.put("enable.auto.commit", "true");
                    configProperties.put("auto.commit.interval.ms", "1000");
                    configProperties.put("session.timeout.ms", "30000");
                } else {
                    System.out.println("Turning auto commit off");

                }

          
                final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configProperties);
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
            } catch (Exception ex) {
                System.out.println("Inside ConsumerThread.run");
            }
        }
    }
}
