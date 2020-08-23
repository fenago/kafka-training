package com.cloudurable.kafka.consumer;
import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SimpleStockPriceConsumer {

    private static Consumer<String, StockPrice> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StockAppConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());


        //TODO Configure the custom Deserializer
        // HINT ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        // HINT StockDeserializer.class.getName()
        // ???????


        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        // Create the consumer using props.
        final Consumer<String, StockPrice> consumer =
                new KafkaConsumer<>(props);


        // TODO Subscribe to the topic.
        // ???????


        return consumer;
    }


    //TODO finish this method.
    static void runConsumer() throws InterruptedException {
        final Consumer<String, StockPrice> consumer = createConsumer();
        final Map<String, StockPrice> map = new HashMap<>();
        try {
            final int giveUp = 1000; int noRecordsCount = 0;
            int readCount = 0;

            while (true) {

                // TODO read ConsumerRecords<String, StockPrice> consumerRecords
                // HINT final ConsumerRecords<String, StockPrice> consumerRecords =
                //        consumer.poll(1000);
                final ConsumerRecords<String, StockPrice> consumerRecords = null; //BROKEN

                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }
                readCount++;

                // Add records to map
                consumerRecords.forEach(record -> {
                    map.put(record.key(), record.value());
                });

                // Display records every 50 iterations
                if (readCount % 50 == 0) {
                    displayRecordsStatsAndStocks(map, consumerRecords);
                }

                // TODO Call commitAsync on producer

            }
        }
        finally {
            consumer.close();
        }
        System.out.println("DONE");
    }

    //TODO finish this method.
    private static void displayRecordsStatsAndStocks(
            final Map<String, StockPrice> stockPriceMap,
            final ConsumerRecords<String, StockPrice> consumerRecords) {

        //TODO print out count and number of partitions.
        // HINT "New ConsumerRecords par count %d count %d\n",

        // TODO PRINT out stocks
        //HINT....
//        stockPriceMap.forEach((s, stockPrice) ->
//                System.out.printf("ticker %s price %d.%d \n",
//                        stockPrice.getName(),
//                        stockPrice.getDollars(),
//                        stockPrice.getCents()));
//        System.out.println();
    }


    public static void main(String... args) throws Exception {
        runConsumer();
    }


}
