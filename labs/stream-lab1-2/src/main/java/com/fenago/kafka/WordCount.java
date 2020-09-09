package com.fenago.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WordCount {
    private static final Logger logger =
            LoggerFactory.getLogger(WordCount.class);

    //TODO ## ***ACTION*** - EDIT WordCount.java and finish main method.
    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();

        // 1 - stream from Kafka
        //TODO Create a KStream for the specified topic.
        //HINT word-count-input
        KStream<String, String> step1Input = builder.stream("????");

        // 2 - map values to lowercase
        //     mapValues(ValueMapper<? super V, ? extends VR> mapper);

        //TODO Use a ValueMapper to transform the input text line (V a string) into lowercase (VR a string)
        //HINT ValueMapper<??V??, ??VR??> toLowerCaseValueMapper = new ValueMapper<??V??, ??VR??>() {
        //HINT     @Override
        //HINT     public ??VR??? apply(??V?? value) {
        //HINT         return value.????();
        //HINT     }
        //HINT };

        // TODO use toLowerCaseValueMapper
        // KStream<String, String> step2Map = step1Input.mapValues(toLowerCaseValueMapper);

        // 3 - flatmap values split by space
        //     flatMapValues(final ValueMapper<? super V, ? extends Iterable<? extends VR>> processor);

        //TODO Use a ValueMapper to flat map the now lowercase lines (a string) into List of strings (VR)
        //HINT ValueMapper<String, ??VR??> flatValueMapper = new ValueMapper<String, ??VR??>() {
        //HINT     @Override
        //HINT     public ??VR??? apply(String value) {
        //HINT         return Arrays.asList(value.split(" "));
        //HINT     }
        //HINT };

        // TODO use flatValueMapper
        // KStream<String, String> step3Flat = step2Map.flatMapValues(flatValueMapper);

        // 4 - select key to apply a key
        //     selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper);

        //TODO Use a KeyValueMapper determine the key (KR a string) of every word (V a string) in the now flat list. We ignore the current key (K a string)
        //HINT KeyValueMapper<??K??, ??V??, ??KR??> keySelector = new KeyValueMapper<??K??, ??V??, ??KR??>() {
        //HINT     @Override
        //HINT     public ??KR?? apply(??K?? key, ??V?? value) {
        //HINT         return value;
        //HINT     }
        //HINT };

        // TODO use keySelector
        // KStream<String, String> step4Key = step3Flat.selectKey(keySelector);

        // TODO 5 - group by key before aggregation
        //     groupByKey()
        // KGroupedStream<String, String> step5Group = step4Key.groupByKey();

        // TODO 6 - count occurences
        //KTable<String, Long> step6Count = step5Group.count("Counts");

        // 7 - to in order to write the results back to kafka
        //TODO write to the specified topic.
        //HINT word-count-output
        //step6Count.to(Serdes.String(), Serdes.Long(), "word-count-output");

        KafkaStreams streams = new KafkaStreams(builder, config);
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
