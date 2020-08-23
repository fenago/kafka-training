package com.cloudurable.kafka.producer;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import io.advantageous.boon.core.Lists;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockPriceKafkaProducer {


    /** Logger. */
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceKafkaProducer.class);


    /**
     * Create Producer.
     * @return producer
     */
    private static Producer<String, StockPrice> createProducer() {

        //TODO Create properties used to configure a new Kafka Producer.

        //HINT: final Properties props = new Properties();

        //TODO Call setupBootstrapAndSerializers passing props.
        //setupBootstrapAndSerializers(props);

        //DEBUG logger.info("Configuring Kafka Producer " + props);

        //TODO create a new KafkaProducer

        //return new KafkaProducer<>(props);

        return null;
    }


    /**
     *
     * @param props properties used to configure Kafka Producer.
     */
    private static void setupBootstrapAndSerializers(Properties props) {

        //TODO Configure serializers.


        //HINT (ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, StockAppConstants.BOOTSTRAP_SERVERS);

        //TODO Configure client id.
        // HINT (ProducerConfig.CLIENT_ID_CONFIG, "StockPriceKafkaProducer");

        //TODO Configure key serializer class to StringSerializer.
        // HINT (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        // HINT        StringSerializer.class.getName());


        //TODO configure Custom Serializer for value - config "value.serializer"
        // HINT (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        // HINT       StockPriceSerializer.class.getName());

    }





    public static void main(String... args) throws Exception {
        //TODO Create Kafka Producer
        //HINT Producer<String, StockPrice> producer

        //TODO Create StockSender list
        // HINT final List<StockSender> stockSenders = getStockSenderList(producer);

        //TODO Create a thread pool so every stock sender gets it own thread.

        //TODO Run each stock sender in its own thread.

    }




    private static List<StockSender> getStockSenderList(
            final Producer<String, StockPrice> producer) {

        //TODO create a list of StockSender
        return Collections.emptyList();

        // HINT BELOW....
//
//        return Lists.list(
//                new StockSender(StockAppConstants.TOPIC,
//                        new StockPrice("IBM", 100, 99),
//                        new StockPrice("IBM", 50, 10),
//                        producer,
//                        1, 10
//               ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("SUN", 100, 99),
//                        new StockPrice("SUN", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("GOOG", 500, 99),
//                        new StockPrice("GOOG", 400, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("INEL", 100, 99),
//                        new StockPrice("INEL", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("UBER", 1000, 99),
//                        new StockPrice("UBER", 50, 0),
//                        producer,
//                        1, 10
//                )
//        );

    }








}














