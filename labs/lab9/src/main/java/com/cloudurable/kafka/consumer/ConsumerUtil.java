package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConsumerUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerUtil.class);

    public static final String FIRST_CLUSTER = "localhost:9092";
    public static final String SECOND_CLUSTER = "localhost:9093";
    public static final String THIRD_CLUSTER = "localhost:9094";

    private static int getPartitionCount(Consumer<String, StockPrice> consumer) {
        //Get the partitions.
        final List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC);

        return partitionInfos.size();
    }


    private static Consumer<String, StockPrice> createConsumer(
            final String bootstrapServers) {

        final Properties props = new Properties();

        //Turn off auto commit - "enable.auto.commit".
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
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

    public static void startConsumers(final String cluster) {
        final Consumer<String, StockPrice> consumer = createConsumer(cluster);
        final int threadCount = getPartitionCount(consumer);
        final ExecutorService executorService = newFixedThreadPool(threadCount);

        final List<StockPriceConsumerRunnable> workers = new ArrayList<>(threadCount);

        IntStream.range(0, threadCount).forEach(index -> {
            final StockPriceConsumerRunnable stockPriceConsumer =
                    new StockPriceConsumerRunnable(createConsumer(cluster),
                            5, index);
            workers.add(stockPriceConsumer);
            executorService.submit(stockPriceConsumer);
        });

        //Register nice shutdown of thread pool, then flush and close producer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            workers.forEach(worker -> worker.setRunning(false));
            consumer.wakeup();

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

}
