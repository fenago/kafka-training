package example.partition;

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
    public static void main(String[] argv)throws Exception{
            if (argv.length != 1) {
                System.err.println("Please specify 1 parameters ");
                System.exit(-1);
            }
            in = new Scanner(System.in);
            final String topicName = argv[0];

            Consumer.PartitionConumserThread ct = new Consumer.PartitionConumserThread(topicName);
            ct.start();
            String line = "";
            while (!line.equals("exit")) {
                line = in.next();
            }
            stop = true;
            System.out.println("Stopping consumer .....");
            ct.join();


    }


    public static class PartitionConumserThread extends Thread{
        private String topicName;
        public PartitionConumserThread(String topicName) {
            this.topicName = topicName;
        }
        @Override
        public void run() {
            //Initialize the Consumer configuration
            Properties configProperties = new Properties();
        
            configProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("group.id", "partition");

            // Generate Debug message about which topic the consumer is listening to
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
                for (ConsumerRecord<String, String> record : records)
                    System.out.println("Offset -> " +record.offset() + ", Message -> " + record.value());
            }
        }
    }

}
