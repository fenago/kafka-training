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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WordCount {
    private static final Logger logger =
            LoggerFactory.getLogger(WordCount.class);

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        // 1 - stream from Kafka
        KStream<String, String> step1Input = builder.stream("word-count-input");

        // 2 - map values to lowercase
        //     mapValues(ValueMapper<? super V, ? extends VR> mapper);

        ValueMapper<String, String> toLowerCaseValueMapper = new ValueMapper<String, String>() {
            @Override
            public String apply(String value) {
                return value.toLowerCase();
            }
        };

        KStream<String, String> step2Map = step1Input.mapValues(toLowerCaseValueMapper);

        // 3 - flatmap values split by space
        //     flatMapValues(final ValueMapper<? super V, ? extends Iterable<? extends VR>> processor);

        ValueMapper<String, List<String>> flatValueMapper = new ValueMapper<String, List<String>>() {
            @Override
            public List<String> apply(String value) {
                return Arrays.asList(value.split(" "));
            }
        };

        KStream<String, String> step3Flat = step2Map.flatMapValues(flatValueMapper);

        // 4 - select key to apply a key
        //     selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper);

        KeyValueMapper<String, String, String> keySelector = new KeyValueMapper<String, String, String>() {
            @Override
            public String apply(String key, String value) {
                return value;
            }
        };

        KStream<String, String> step4Key = step3Flat.selectKey(keySelector);

        // 5 - group by key before aggregation
        //     groupByKey()
        KGroupedStream<String, String> step5Group = step4Key.groupByKey();

        // 6 - count occurrences
        KTable<String, Long> step6Count = step5Group.count(Materialized.as("Counts"));

        // 7 - to in order to write the results back to kafka

        step6Count.toStream().to("word-count-output", Produced.with(Serdes.String(),
                Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
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
