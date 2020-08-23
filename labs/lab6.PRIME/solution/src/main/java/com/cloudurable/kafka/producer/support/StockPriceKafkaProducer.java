package com.cloudurable.kafka.producer.support;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import io.advantageous.boon.core.Lists;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StockPriceKafkaProducer {

    private static Producer<String, StockPrice>
                                    createProducer() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StockAppConstants.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "StockPriceKafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StockPriceSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,  16_384 * 4);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
                StockPricePartitioner.class.getName());
        props.put("importantStocks", "IBM,UBER");
        return new KafkaProducer<>(props);
    }



    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceKafkaProducer.class);



    public static void main(String... args)
            throws Exception {
        //Create Kafka Producer
        final Producer<String, StockPrice> producer = createProducer();
        //Create StockSender list
        final List<StockSender> stockSenders = getStockSenderList(producer);

        //Create a thread pool so every stock sender gets it own.
        final ExecutorService executorService =
                Executors.newFixedThreadPool(stockSenders.size());

        //Run each stock sender in its own thread.
        stockSenders.forEach(executorService::submit);


        //Register nice shutdown of thread pool, then flush and close producer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
                logger.info("Flushing and closing producer");
                producer.flush();
                producer.close(10_000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("shutting down", e);
            }
        }));
    }




    private static List<StockSender> getStockSenderList(
            final Producer<String, StockPrice> producer) {
        return Lists.list(
                new StockSender(StockAppConstants.TOPIC,
                        new StockPrice("IBM", 100, 99),
                        new StockPrice("IBM", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("UBER", 1000, 99),
                        new StockPrice("UBER", 50, 0),
                        producer,
                        100, 1000                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("SUN", 100, 99),
                        new StockPrice("SUN", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("GOOG", 500, 99),
                        new StockPrice("GOOG", 400, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("INEL", 100, 99),
                        new StockPrice("INEL", 50, 10),
                        producer,
                        100, 1000                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("ABC", 100, 99),
                        new StockPrice("ABC", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("XYZ", 100, 99),
                        new StockPrice("XYZ", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("DEF", 100, 99),
                        new StockPrice("DEF", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("DEF", 100, 99),
                        new StockPrice("DEF", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("AAA", 100, 99),
                        new StockPrice("AAA", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("BBB", 100, 99),
                        new StockPrice("BBB", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("CCC", 100, 99),
                        new StockPrice("CCC", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("DDD", 100, 99),
                        new StockPrice("DDD", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("EEE", 100, 99),
                        new StockPrice("EEE", 50, 10),
                        producer,
                        100, 1000
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("FFF", 100, 99),
                        new StockPrice("FFF", 50, 10),
                        producer,
                        100, 1000
                )
        );

    }








}














