package com.fenago.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WordCountAlternate {
    private static final Logger logger =
            LoggerFactory.getLogger(WordCount.class);

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // 2 - map values to lowercase
        //     mapValues(ValueMapper<? super V, ? extends VR> mapper);
        // 3 - flatmap values split by space
        //     flatMapValues(final ValueMapper<? super V, ? extends Iterable<? extends VR>> processor);
        // 4 - select key to apply a key
        //     selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper);
        // 5 - group by key before aggregation
        //     groupByKey()
        // 6 - count occurrences


        StreamsBuilder builder = new StreamsBuilder();

        // 1 - stream from Kafka
        KStream<String, String> input = builder.stream("word-count-input");
        //...
        KTable<String, Long> wordCounts = input
                .mapValues((ValueMapper<String, String>) String::toLowerCase)
                .flatMapValues(textLine -> Arrays.asList(textLine.split(" ")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count(Materialized.as("Counts"));

        // 7 - to in order to write the results back to kafka
        wordCounts.toStream().to("word-count-output",
                Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        registerCleanShutdown(streams);
    }

    private static void registerCleanShutdown(KafkaStreams streams) {
        //Register nice shutdown of thread pool, then flush and close producer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Flushing and closing streams");
            streams.close(10_000, TimeUnit.MILLISECONDS);
        }));
    }
}
