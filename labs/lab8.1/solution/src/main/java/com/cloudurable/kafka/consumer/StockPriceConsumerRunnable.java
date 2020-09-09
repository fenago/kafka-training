package com.fenago.kafka.consumer;

import com.fenago.kafka.StockAppConstants;
import com.fenago.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class StockPriceConsumerRunnable implements Runnable {
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceConsumerRunnable.class);

    private final Consumer<String, StockPrice> consumer;
    private final int readCountStatusUpdate;
    private final int threadIndex;
    private final int delayMS;
    private boolean running = true;
    private final String topic;


    public StockPriceConsumerRunnable(final Consumer<String, StockPrice> consumer,
                                      final int readCountStatusUpdate,
                                      final int threadIndex,
                                      final int delayMS,
                                      final String topic) {
        this.consumer = consumer;
        this.readCountStatusUpdate = readCountStatusUpdate;
        this.threadIndex = threadIndex;
        this.delayMS = delayMS;
        this.topic = topic;
    }


    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    void runConsumer() throws Exception {
        consumer.subscribe(Collections.singleton(topic));
        final Map<String, StockPriceRecord> lastRecordPerStock = new HashMap<>();
        try {
            int readCount = 0;
            while (isRunning()) {
                readCount++;
                pollRecordsAndProcess(lastRecordPerStock, readCount);
            }
        } finally {
            consumer.close();
        }
    }


    private void pollRecordsAndProcess(
            final Map<String, StockPriceRecord> currentStocks,
            final int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(100);

        if (consumerRecords.count() == 0) {
            if (readCount % readCountStatusUpdate == 0) {
                displayRecordsStatsAndStocks(currentStocks, consumerRecords);
            }
            return;
        }


        consumerRecords.forEach(record ->
                currentStocks.put(record.key(),
                        new StockPriceRecord(record.value(), true, record)
                ));

        processRecords(consumerRecords);

        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(currentStocks, consumerRecords);
        }

    }


    private void processRecords(final ConsumerRecords<String, StockPrice> consumerRecords) {

        consumerRecords.forEach(this::processRecord);
        consumer.commitSync();
    }


    private void processRecord(ConsumerRecord<String, StockPrice> record) {

        try {
            Thread.sleep(delayMS);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }


    private void displayRecordsStatsAndStocks(
            final Map<String, StockPriceRecord> stockPriceMap,
            final ConsumerRecords<String, StockPrice> consumerRecords) {


        final long max = getMaxOffset(consumerRecords);

        System.out.printf("New ConsumerRecords par count %d count %d, max offset %d\n",
                consumerRecords.partitions().size(),
                consumerRecords.count(), max);
        stockPriceMap.forEach((s, stockPrice) ->
                System.out.printf("ticker %s price %d.%d saved %s Thread %d \n",
                        stockPrice.getName(),
                        stockPrice.getDollars(),
                        stockPrice.getCents(),
                        stockPrice.isSaved(),
                        threadIndex));
        System.out.println();
    }

    private long getMaxOffset(ConsumerRecords<String, StockPrice> consumerRecords) {
        AtomicLong aMax = new AtomicLong();
        consumerRecords.records(topic).forEach(record -> {
            if (record.offset() > aMax.get()) {
                aMax.set(record.offset());
            }
        });

        return aMax.get();
    }

    @Override
    public void run() {
        try {
            runConsumer();
        } catch (Exception ex) {
            logger.error("Run Consumer Exited with", ex);
            throw new RuntimeException(ex);
        }
    }
}
