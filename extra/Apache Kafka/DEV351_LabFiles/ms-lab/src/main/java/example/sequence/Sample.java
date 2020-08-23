package example.sequence;

/**
 * To run: 
 * java -cp `mapr classpath`:ms-lab-1.0.jar example.sequence.Sample send /user/user01/pump:input 50000 
 * java -cp `mapr classpath`:ms-lab-1.0.jar example.sequence.Sample get /user/user01/pump:input 5
 *
 */
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Sample {

    public static void main(String[] args) throws Exception {
        boolean sendMessages;

        String directionStr = args[0];
        if ("send".equals(directionStr)) {
            sendMessages = true;
        } else {
            sendMessages = false;
        }

        String topic = args[1];
        long num = Long.valueOf(args[2]);

        if (sendMessages) {
            System.out.println("Sending " + num + " to topic " + topic);
        } else {
            System.out.println("Getting messages from topic " + topic + " for "
                    + num + " seconds");
        }

        Properties props = new Properties();

        // cause consumers to start at beginning of topic on first read
        props.put("auto.offset.reset", "earliest");

        props.put("client.id", "testclient"); //my id for status info

        // consumer group for cursor tracking and topic sharing
        props.put("group.id", "myteam");
        System.out.println("Using group.id = myteam");

        if (sendMessages) {
            long numOfMsgs = num;

            Producer<String, String> producer = new KafkaProducer<String, String>(props, new org.apache.kafka.common.serialization.StringSerializer(), new org.apache.kafka.common.serialization.StringSerializer());
        

            String value = "myvalue" + new Date() + "::";

            System.out.print("sending...");

            for (int i = 0; i < numOfMsgs; i++) {

                ProducerRecord<String, String> rec = new ProducerRecord<String, String>(
                        topic, null, value + i);
                System.out.print(".");

                Future<RecordMetadata> future = producer.send(rec);
            }
            System.out.println();

            producer.close();
        } else {
            long waitTime = num * 1000;
            long numberOfMsgsReceived = 0;

            Consumer<String, String> consumer = new KafkaConsumer<String, String>(
                    props,
                    new org.apache.kafka.common.serialization.StringDeserializer(),
                    new org.apache.kafka.common.serialization.StringDeserializer());
    
            consumer.subscribe(Arrays.asList(topic), new SaveCursor(consumer));
            while (waitTime > 0) {
                System.out.println("Wait 1 second for messages (" + waitTime / 1000 + " seconds left)");
                ConsumerRecords<String, String> msg = consumer.poll(1000);
                if (msg.count() == 0) {
                    System.out.println("No messages after 1 second wait.");
                } else {
                    System.out.println("Read " + msg.count() + " messages");
                    numberOfMsgsReceived += msg.count();
                    Iterator<ConsumerRecord<String, String>> iter = msg
                            .iterator();
                    while (iter.hasNext()) {
                        ConsumerRecord<String, String> record = iter.next();
                        System.out.println(record.toString() + "(length = " + record.value().length() + ")");
                    }
                }
                waitTime = waitTime - 1000;

            }
            System.out.println("Calling commit");
            consumer.commitSync();
      

            System.out.println("Called commit");

            System.out.println("Calling consumer.close()");
            System.out.println("Total number of messages received: " + numberOfMsgsReceived);
            consumer.close();

        }
    }
}
