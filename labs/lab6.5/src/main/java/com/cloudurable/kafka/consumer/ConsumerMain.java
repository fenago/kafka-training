package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConsumerMain {
    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerMain.class);


    private static Consumer<String, StockPrice> createConsumer() {
        final Properties props = new Properties();

        //Turn off auto commit - "enable.auto.commit".
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StockAppConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        //Custom Deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StockDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);


        // Create the consumer using props.
        return new KafkaConsumer<>(props);
    }

    //TODO finish this method
    public static void main(String... args) throws Exception {
        final int threadCount = 5;

        //TODO Create a thread pool
        final ExecutorService executorService = null;  // BROKE... FIX ME... HINT newFixedThreadPool(threadCount);
        final AtomicBoolean stopAll = new AtomicBoolean();


        IntStream.range(0, threadCount).forEach(index -> {

            //TODO create StockPriceConsumerRunnable and submit it to the executorService
            //HINT    new StockPriceConsumerRunnable(createConsumer(),
            //                1000, index, stopAll);
            //HINT executorService.submit(???);
        });

        //Register nice shutdown of thread pool, then flush and EXTRA CREDIT close the consumers.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping app");
            stopAll.set(true);

            //TODO EXTRA CREDIT call wake up on the list of running consumers.
            // Keep a list of the consumers when you start up the threads above.
            sleep();
            executorService.shutdown();
            try {
                executorService.awaitTermination(5_000, TimeUnit.MILLISECONDS);
                if (!executorService.isShutdown())
                    executorService.shutdownNow();
            } catch (InterruptedException e) {
                logger.warn("shutting down", e);
            }
        }));
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

}
