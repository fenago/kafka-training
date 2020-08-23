package example.group;

import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.Scanner;

/**
 *  
 */
public class Producer {
    private static final Logger logger = Logger.getLogger(Producer.class);
    private static Scanner in;

    public static void main(String[] argv){
        if(argv.length !=1){
            System.err.println("Please specify 1 parameters ");
            System.exit(-1);
        }
        String topicName = argv[0];
        in = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();
   
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        String line = in.nextLine();

        //Listen to user input and send every message to server
        while(!line.equals("exit")) {
           System.out.println("Sending message -> " + line);
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicName,  line);
            producer.send(rec, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println( "Message Sent Successfully Topic -> " + recordMetadata.topic() + " Partition -> " + recordMetadata.partition() +" Offset-> " + recordMetadata.offset());
                }
            });
            line = in.nextLine();
        }
        in.close();
        producer.close();
    }
}
