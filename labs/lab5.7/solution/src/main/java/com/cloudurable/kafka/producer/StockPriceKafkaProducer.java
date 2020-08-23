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

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StockPriceKafkaProducer {


    /** Logger. */
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceKafkaProducer.class);


    private static Producer<String, StockPrice> createProducer() {
        final Properties props = new Properties();
        setupBootstrapAndSerializers(props);
        setupBatchingAndCompression(props);
        setupRetriesInFlightTimeout(props);

        //TODO Install interceptor list - config "interceptor.classes"
        //      Set to StockProducerInterceptor.class.getName()
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                StockProducerInterceptor.class.getName());


        logger.info("Configuring Kafka Producer " + props);

        return new KafkaProducer<>(props);
    }


    private static void setupRetriesInFlightTimeout(Properties props) {
        //TODO Set Only one in-flight messages per Kafka broker connection (default 5)
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        //TODO Set the number of retries to 3.
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        //TODO Set Request timeout to 15 seconds - request.timeout.ms
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);

        //TODO Set retry after one second.
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    }

    private static void setupBatchingAndCompression(final Properties props) {

        //Linger up to 50 ms before sending batch if size not met
        props.put(ProducerConfig.LINGER_MS_CONFIG, 50);

        //Batch up to 64K buffer sizes.
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,  16_384 * 4);

        //Use Snappy compression for batch compression.
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    }

    /**
     *
     * @param props properties used to configure Kafka Producer.
     */
    private static void setupBootstrapAndSerializers(Properties props) {

        //Configure serializers.
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StockAppConstants.BOOTSTRAP_SERVERS);

        //Configure client id.
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "StockPriceKafkaProducer");

        //Configure key serializer class to StringSerializer.
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());


        //Custom Serializer - config "value.serializer"
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StockPriceSerializer.class.getName());


        props.put(ProducerConfig.ACKS_CONFIG, "all");



    }





    public static void main(String... args) throws Exception {
        //Create Kafka Producer
        final Producer<String, StockPrice> producer = createProducer();
        //Create StockSender list
        final List<StockSender> stockSenders = getStockSenderList(producer);

        //Create a thread pool so every stock sender gets it own thread.
        // Increase by 1 to fit metrics.
        final ExecutorService executorService =
                Executors.newFixedThreadPool(stockSenders.size() + 1);

        //Run Metrics Producer Reporter which is runnable passing it the producer.
        executorService.submit(new MetricsProducerReporter(producer));

        //Run each stock sender in its own thread.
        stockSenders.forEach(executorService::submit);


        registerCleanShutdown(producer, executorService);


    }

    private static void registerCleanShutdown(Producer<String, StockPrice> producer,
                                              ExecutorService executorService) {
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
                        1, 10
               ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("SUN", 100, 99),
                        new StockPrice("SUN", 50, 10),
                        producer,
                        1, 10
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("GOOG", 500, 99),
                        new StockPrice("GOOG", 400, 10),
                        producer,
                        1, 10
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("INEL", 100, 99),
                        new StockPrice("INEL", 50, 10),
                        producer,
                        1, 10
                ),
                new StockSender(
                        StockAppConstants.TOPIC,
                        new StockPrice("UBER", 1000, 99),
                        new StockPrice("UBER", 50, 0),
                        producer,
                        1, 10
                )
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("ABC", 100, 99),
//                        new StockPrice("ABC", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("XYZ", 100, 99),
//                        new StockPrice("XYZ", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("DEF", 100, 99),
//                        new StockPrice("DEF", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("DEF", 100, 99),
//                        new StockPrice("DEF", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("AAA", 100, 99),
//                        new StockPrice("AAA", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("BBB", 100, 99),
//                        new StockPrice("BBB", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("CCC", 100, 99),
//                        new StockPrice("CCC", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("DDD", 100, 99),
//                        new StockPrice("DDD", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("EEE", 100, 99),
//                        new StockPrice("EEE", 50, 10),
//                        producer,
//                        1, 10
//                ),
//                new StockSender(
//                        StockAppConstants.TOPIC,
//                        new StockPrice("FFF", 100, 99),
//                        new StockPrice("FFF", 50, 10),
//                        producer,
//                        1, 10
//                )
        );

    }


}














