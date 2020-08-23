package example.simple;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.Scanner;

/**
 * 
 */
public class Producer {

    private static Scanner in;

    public static void main(String[] argv) throws Exception {
        if (argv.length != 1) {
            System.err.println("Please specify 1 parameters ");
            System.exit(-1);
        }
        String topicName = argv[0];
        in = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();

        configProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        String line = in.nextLine();
        while (!line.equals("exit")) {
            System.out.println("Sending message -> " + line);
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicName, 0, null, line);
            producer.send(rec, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("Message Sent to Topic -> " + recordMetadata.topic()
                            + " Offset-> " + recordMetadata.offset());
                }
            });
            line = in.nextLine();
        }
        in.close();
        producer.close();
    }
}
