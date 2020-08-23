package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.cloudurable.kafka.consumer.DatabaseUtilities.getConnection;
import static com.cloudurable.kafka.consumer.DatabaseUtilities.saveStockPrice;
import static com.cloudurable.kafka.consumer.DatabaseUtilities.startJdbcTransaction;

public class SimpleStockPriceConsumer {
    private static final Logger logger =
            LoggerFactory.getLogger(SimpleStockPriceConsumer.class);


    private static Consumer<String, StockPrice> createConsumer() {
        final Properties props = initProperties();

        // Create the consumer using props.
        final Consumer<String, StockPrice> consumer =
                new KafkaConsumer<>(props);



        return consumer;
    }


    private static Properties initProperties() {
        final Properties props = new Properties();
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
        return props;
    }


    //TODO finish this method
    private static void runConsumer(final int readCountStatusUpdate) throws InterruptedException {
        final Map<String, StockPriceRecord> map = new HashMap<>();


        try (final Consumer<String, StockPrice> consumer = createConsumer()) {

            // TODO subscribe to this topic using SeekToLatestRecordsConsumerRebalanceListener
            // ????
            // HINT consumer.subscribe(Collections.singletonList(StockAppConstants.TOPIC),
            //        new SeekToLatestRecordsConsumerRebalanceListener(consumer));

            int readCount = 0;
            while (true) {
                try {
                    pollRecordsAndProcess(readCountStatusUpdate, consumer, map, readCount);
                } catch (Exception e) {
                    logger.error("Problem handling record processing", e);
                }
                readCount ++;
            }
        }
    }

    // TODO finish this method
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPriceRecord> currentStocks, int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(1000);

        if (consumerRecords.count() == 0) return;


        //Get rid of duplicates and keep only the latest record.
        consumerRecords.forEach(record -> currentStocks.put(record.key(),
                new StockPriceRecord(record.value(), false, record)));

        final Connection connection = getConnection();
        try {
            //TODO Start DB Transaction
            for (StockPriceRecord stockRecordPair : currentStocks.values()) {
                if (!stockRecordPair.isSaved()) {
                    //Save the record
                    // with partition/offset to DB.
                    saveStockPrice(stockRecordPair, connection);
                    //Mark the record as saved
                    currentStocks.put(stockRecordPair.getName(), new
                            StockPriceRecord(stockRecordPair, true));
                }
            }

            // TODO Commit DB Transaction
            // TODO Commit the Kafka offset
        } catch (CommitFailedException ex) {
            logger.error("Failed to commit sync to log", ex);
            connection.rollback();                      //Rollback Transaction
        } catch (SQLException sqle) {
            logger.error("Failed to write to DB", sqle);
            connection.rollback();                      //Rollback Transaction
        } finally {
            connection.close();
        }


        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(currentStocks, consumerRecords);
        }
    }


    private static void displayRecordsStatsAndStocks(
            final Map<String, StockPriceRecord> stockPriceMap,
            final ConsumerRecords<String, StockPrice> consumerRecords) {

        System.out.printf("New ConsumerRecords par count %d count %d, max offset %d\n",
                consumerRecords.partitions().size(),
                consumerRecords.count(), getHighestOffset(consumerRecords));
        stockPriceMap.forEach((s, stockPrice) ->
                System.out.printf("ticker %s price %d.%d saved %s\n",
                        stockPrice.getName(),
                        stockPrice.getDollars(),
                        stockPrice.getCents(),
                        stockPrice.isSaved()));
        System.out.println();
        System.out.println("Database Records");
        DatabaseUtilities.readDB().forEach(stockPriceRecord ->
                System.out.printf("ticker %s price %d.%d saved from %s-%d-%d\n",
                        stockPriceRecord.getName(),
                        stockPriceRecord.getDollars(),
                        stockPriceRecord.getCents(),
                        stockPriceRecord.getTopic(),
                        stockPriceRecord.getPartition(),
                        stockPriceRecord.getOffset()));
        System.out.println();
    }



    private static long getHighestOffset(ConsumerRecords<String, StockPrice> consumerRecords) {
        long maxOffsetSeen = 0;
        for (ConsumerRecord record : consumerRecords) {
            if (record.offset() > maxOffsetSeen) {
                maxOffsetSeen = record.offset();
            }
        }
        return maxOffsetSeen;
    }


    public static void main(String... args) throws Exception {

        DatabaseUtilities.initDB();

        int readCountStatusUpdate = 100;
        if (args.length == 1) {
            readCountStatusUpdate = Integer.parseInt(args[2]);
        }
        runConsumer(readCountStatusUpdate);
    }

}
