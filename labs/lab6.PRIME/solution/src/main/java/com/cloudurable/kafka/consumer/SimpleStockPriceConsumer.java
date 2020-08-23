package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.*;

import static com.cloudurable.kafka.consumer.DatabaseUtilities.*;

public class SimpleStockPriceConsumer {
    private static final Logger logger =
            LoggerFactory.getLogger(SimpleStockPriceConsumer.class);

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
        final Consumer<String, StockPrice> consumer =
                new KafkaConsumer<>(props);


        return consumer;
    }


    static void runConsumer(final int readCountStatusUpdate)
            throws Exception {

        //Create consumer
        final Consumer<String, StockPrice> consumer = createConsumer();
        final boolean running = true;

        consumer.subscribe(Collections.singletonList(StockAppConstants.TOPIC),
                new SeekToLatestRecordsConsumerRebalanceListener(consumer));

        final Map<String, StockPriceRecord> lastRecordPerStock = new HashMap<>();

        //Initialize current records with what is in DB.
        readDB().forEach(stockPriceRecord -> {
                lastRecordPerStock.put(stockPriceRecord.getName(), stockPriceRecord);
        });

        try {
            int readCount = 0;
            while (running) {
                    pollRecordsAndProcess(readCountStatusUpdate,
                            consumer, lastRecordPerStock, readCount);
            }
        } finally {
            consumer.close();
        }
    }


    private static void pollRecordsAndProcess(
            final int readCountStatusUpdate,
            final Consumer<String, StockPrice> consumer,
            final Map<String, StockPriceRecord> currentStocks,
            final int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(1000);

        if (consumerRecords.count() == 0) return;


        //Get rid of duplicates and keep only the latest record.
        consumerRecords.forEach(record -> currentStocks.put(record.key(),
                new StockPriceRecord(record.value(), false, record)));

        final Connection connection = getConnection();
        try {
            startJdbcTransaction(connection);               //Start DB Transaction
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
            consumer.commitSync();                      //Commit the Kafka offset
            connection.commit();                        //Commit DB Transaction
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

        initDB();

        runConsumer(10);
    }


//    static void runConsumer(final SeekTo seekTo, final long location,
//                            final int readCountStatusUpdate)
//            throws Exception {
//
//        //Create consumer
//        final Consumer<String, StockPrice> consumer = createConsumer();
//        final boolean running = true;
//
//        //call position consumer
//
//        final Map<String, StockPriceRecordPair> lastRecordPerStock = new HashMap<>();
//        try {
//            int readCount = 0;
//            while (running) {
//                pollRecordsAndProcess(readCountStatusUpdate,
//                        consumer, lastRecordPerStock, readCount);
//            }
//        } finally {
//            consumer.close();
//        }
//    }

//    private static void positionConsumer(final Consumer<String, StockPrice> consumer,
//                                         final SeekTo seekTo,
//                                         final long location) {
//
//        // Subscribe to the topic.
//        consumer.subscribe(Collections.singletonList(
//                StockAppConstants.TOPIC),
//                new SeekToConsumerRebalanceListener(consumer, seekTo, location));
//
//        logger.info(String.format("Position Consumer %s %d",
//                seekTo, location));
//
//
//    }
    // from runConsumer
    //Position the consumer
    // positionConsumer(consumer, seekTo, location);

    //Fine grained starter code for at-once and at-most
//    private static void pollRecordsAndProcess(
//            int readCountStatusUpdate,
//            Consumer<String, StockPrice> consumer,
//            Map<String, StockPrice> map, int readCount) {
//
//        final ConsumerRecords<String, StockPrice> consumerRecords =
//                consumer.poll(1000);
//
//
//        consumerRecords.forEach(record -> {
//            try {
//                startTransaction();         //Start DB Transaction
//
//                // Commit Kafka at exact location for record, and only this record.
//                final TopicPartition recordTopicPartition =
//                        new TopicPartition(record.topic(), record.partition());
//
//                final Map<TopicPartition, OffsetAndMetadata> commitMap =
//                        Collections.singletonMap(recordTopicPartition,
//                                new OffsetAndMetadata(record.offset() + 1));
//
//                consumer.commitSync(commitMap); //Kafka Commit
//
//                processRecord(record);      //Process the record
//
//                commitTransaction();        //Commit DB Transaction
//            } catch (CommitFailedException ex) {
//                logger.error("Failed to commit sync to log", ex);
//                rollbackTransaction();      //Rollback Transaction
//            } catch (DatabaseException dte) {
//                logger.error("Failed to write to DB", dte);
//                rollbackTransaction();      //Rollback Transaction
//            }
//        });
//
//
//        if (readCount % readCountStatusUpdate == 0) {
//            displayRecordsStatsAndStocks(map, consumerRecords);
//        }
//    }

    //  Bulk at once and at most code
//    private static void pollRecordsAndProcess(
//            int readCountStatusUpdate,
//            Consumer<String, StockPrice> consumer,
//            Map<String, StockPrice> map, int readCount) {
//
//        final ConsumerRecords<String, StockPrice> consumerRecords =
//                consumer.poll(1000);
//
//        try {
//            startTransaction();         //Start DB Transaction
//
//            //Commit the Kafka offset
//            consumer.commitSync();
//
//            //Process the records
//            processRecords(map, consumerRecords);
//
//            commitTransaction();        //Commit DB Transaction
//        } catch(CommitFailedException ex) {
//            logger.error("Failed to commit sync to log", ex);
//            rollbackTransaction();      //Rollback Transaction
//        } catch (DatabaseException dte) {
//            logger.error("Failed to write to DB", dte);
//            rollbackTransaction();      //Rollback Transaction
//        }
//
//
//        if (readCount % readCountStatusUpdate == 0) {
//            displayRecordsStatsAndStocks(map, consumerRecords);
//        }
//    }
//
//    private static void processRecord(ConsumerRecord<String, StockPrice> record) {
//
//    }
//
//    private static void commitTransaction() {
//    }
//
//    private static void rollbackTransaction() {
//    }
//
//    private static void startTransaction() {
//
//    }
//
    //    public static void main(String... args) throws Exception {
//
//        SeekTo seekTo = SeekTo.NONE; // SeekTo what?
//        long location = -1; // Location to seek to if SeekTo.Location
//        int readCountStatusUpdate = 100;
//        if (args.length >= 1) {
//            seekTo = SeekTo.valueOf(args[0].toUpperCase());
//            if (seekTo.equals(SeekTo.LOCATION)) {
//                location = Long.parseLong(args[1]);
//            }
//        }
//        if (args.length == 3) {
//            readCountStatusUpdate = Integer.parseInt(args[2]);
//        }
//        runConsumer(seekTo, location, readCountStatusUpdate);
//    }


}
