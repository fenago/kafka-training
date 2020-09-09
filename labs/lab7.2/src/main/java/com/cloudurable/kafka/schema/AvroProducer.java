package com.fenago.kafka.schema;

import com.fenago.phonebook.Employee;
import com.fenago.phonebook.PhoneNumber;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.util.Properties;
import java.util.stream.IntStream;

public class AvroProducer {

    private static Producer<Long, Employee> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());

        // TODO Configure the KafkaAvroSerializer.
        // HINT ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG KafkaAvroSerializer.class.getName())

        // TODO Set the Schema Registry location use README as a guide.
        // HINT KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG localhost:8081.

        return new KafkaProducer<>(props);
    }

    private final static String TOPIC = "new-employees";

    public static void main(String... args) {

        Producer<Long, Employee> producer = createProducer();


        IntStream.range(1, 100).forEach(index->{

            //TODO create an employee object and send it with producer.
//           HINT Employee.newBuilder().setAge(35)
//                    .setFirstName("Bob")
//                    .setLastName("Jones")
//                    .setPhoneNumber(
//                            PhoneNumber.newBuilder()
//                                    .setAreaCode("301")
//                                    .setCountryCode("1")
//                                    .setPrefix("555")
//                                    .setNumber("1234")
//                                    .build())
//                    .build();
            // HINT producer.send(new ProducerRecord<>(TOPIC, 1L * index, ???));

        });

        producer.flush();
        producer.close();
    }

}
