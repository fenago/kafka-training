package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;


// TODO implement Runnable
public class StockPriceConsumerRunnable {
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceConsumerRunnable.class);

    private final Consumer<String, StockPrice> consumer;
    private final int readCountStatusUpdate;
    private final int threadIndex;
    private final AtomicBoolean stopAll;
    private boolean running = true;


    //TODO implement run method
    public void run() {
        // TODO call runConsumer, catch exceptions and log them.
        // HINT    runConsumer();
    }


    public StockPriceConsumerRunnable(final Consumer<String, StockPrice> consumer,
                                      final int readCountStatusUpdate,
                                      final int threadIndex,
                                      final AtomicBoolean stopAll) {
        this.consumer = consumer;
        this.readCountStatusUpdate = readCountStatusUpdate;
        this.threadIndex = threadIndex;
        this.stopAll = stopAll;
    }


    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    void runConsumer() throws Exception {
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        final Map<String, StockPrice> lastRecordPerStock = new HashMap<>();
        try {
            int readCount = 0;
            while (isRunning()) {
                pollRecordsAndProcess(lastRecordPerStock, readCount);
            }
        } finally {
            consumer.close();
        }
    }


    private void pollRecordsAndProcess(
            final Map<String, StockPrice> currentStocks,
            final int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(100);

        if (consumerRecords.count() == 0) {
            if (stopAll.get()) this.setRunning(false);
            return;
        }

        try {
            startTransaction();                         //Start DB Transaction

            processRecords(currentStocks, consumerRecords);
            consumer.commitSync();                      //Commit the Kafka offset
            commitTransaction();                        //Commit DB Transaction
        } catch (CommitFailedException ex) {
            logger.error("Failed to commit sync to log", ex);
            rollbackTransaction();                      //Rollback Transaction
        }


        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(currentStocks, consumerRecords);
        }
    }

    private void processRecord(ConsumerRecord<String, StockPrice> record) {

    }

    private void commitTransaction() {
    }

    private void rollbackTransaction() {
    }

    private void startTransaction() {

    }

    private void processRecords(Map<String, StockPrice> currentStocks,
                                ConsumerRecords<String, StockPrice> consumerRecords) {

        consumerRecords.forEach(record -> currentStocks.put(record.key(), record.value()));
    }

    private void displayRecordsStatsAndStocks(
            final Map<String, StockPrice> stockPriceMap,
            final ConsumerRecords<String, StockPrice> consumerRecords) {

        System.out.printf("New ConsumerRecords par count %d count %d, max offset\n",
                consumerRecords.partitions().size(),
                consumerRecords.count());
        stockPriceMap.forEach((s, stockPrice) ->
                System.out.printf("ticker %s price %d.%d Thread %d\n",
                        stockPrice.getName(),
                        stockPrice.getDollars(),
                        stockPrice.getCents(),
                        threadIndex));
        System.out.println();
    }

}
