package com.fenago.kafka.consumer;

import com.fenago.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fenago.kafka.StockAppConstants.TOPIC;


public class StockPriceConsumerRunnable implements Runnable {
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceConsumerRunnable.class);

    private final Consumer<String, StockPrice> consumer;
    private final int readCountStatusUpdate;
    private final int threadIndex;
    private final AtomicBoolean stopAll;
    private boolean running = true;

    //TODO Store blocking queue by Topic Partition.
    // HINT Map<TopicPartition, BlockingQueue<ConsumerRecord>> commitQueueMap =
    // HINT new ConcurrentHashMap<>();

    //TODO define Worker pool named threadPool that is of type ExecutorService.
    // ???


    public StockPriceConsumerRunnable(final Consumer<String, StockPrice> consumer,
                                      final int readCountStatusUpdate,
                                      final int threadIndex,
                                      final AtomicBoolean stopAll,
                                      final int numWorkers) {
        this.consumer = consumer;
        this.readCountStatusUpdate = readCountStatusUpdate;
        this.threadIndex = threadIndex;
        this.stopAll = stopAll;

        // TODO initialize the thread pool based on the numWorkers
        // this.threadPool = Executors.newFixedThreadPool(numWorkers);
    }


    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    private void runConsumer() throws Exception {
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        final Map<String, StockPrice> lastRecordPerStock = new ConcurrentHashMap<>();

        int readCount = 0;
        while (isRunning()) {
            pollRecordsAndProcess(lastRecordPerStock, readCount);
            readCount++;
        }
    }

    //TODO finish this method.
    private void pollRecordsAndProcess(
            final Map<String, StockPrice> currentStocks,
            final int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords = consumer.poll(100);

        if (consumerRecords.count() == 0) {
            if (stopAll.get()) this.setRunning(false);
            return;
        }

        // TODO Run processRecords from a thread in the thread pool.
        // HINT threadPool.submit(() -> processRecords(currentStocks, consumerRecords));

        // TODO Call processCommits
        // ???

        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(currentStocks, consumerRecords);
        }
    }


    //TODO finish this method.
    private void processRecords(final Map<String, StockPrice> currentStocks,
                                final ConsumerRecords<String, StockPrice> consumerRecords) {

        consumerRecords.forEach(record ->
                currentStocks.put(record.key(), record.value()));

        consumerRecords.forEach(record -> {

            try {
                startTransaction();           //Start DB Transaction
                processRecord(record);
                commitTransaction();          //Commit DB Transaction

                // TODO Call commitRecordOffsetToKafka
                // HINT: commitRecordOffsetToKafka(record); //Send record to commit queue for Kafka
            } catch (DatabaseException dbe) {
                rollbackTransaction();
            }
        });

    }

    //TODO finish this method.
    private void commitRecordOffsetToKafka(ConsumerRecord<String, StockPrice> record) {

        //TODO create a TopicPartition from the record

        //TODO TopicPartition to lookup a queue, and create if not present using  computeIfAbsent
        //  HINT final BlockingQueue<ConsumerRecord> queue = commitQueueMap.computeIfAbsent
                // topicPartition,
                // k -> new LinkedTransferQueue<>()

        //TODO Add a record to the queue
        // HINT queue.add(record);
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

    @Override
    public void run() {
        try {
            runConsumer();
        } catch (Exception ex) {
            logger.error("Run Consumer Exited with", ex);
            throw new RuntimeException(ex);
        }
    }

    //TODO finish this method
    private void processCommits() {

        //TODO iterate through the map and look up queues, and process them.

//        commitQueueMap.entrySet().forEach(queueEntry -> {
//            final BlockingQueue<ConsumerRecord> queue = queueEntry.getValue();
//            final TopicPartition topicPartition = queueEntry.getKey();
//
//            ConsumerRecord consumerRecord = queue.poll();
//            ConsumerRecord highestOffset = consumerRecord;
//
//            while (consumerRecord != null) {
//                if (consumerRecord.offset() > highestOffset.offset()) {
//                    highestOffset = consumerRecord;
//                }
//                consumerRecord = queue.poll();
//            }
//
//
//            if (highestOffset != null) {
//                try {
//                    logger.info(String.format("Sending commit %s %d", topicPartition,
//                            highestOffset.offset()));
//                    consumer.commitSync(Collections.singletonMap(topicPartition,
//                            new OffsetAndMetadata(highestOffset.offset())));
//                } catch (CommitFailedException cfe) {
//                    logger.error("Failed to commit record", cfe);
//                }
//            }
//        });
    }

    private void commitTransaction() {
    }

    private void rollbackTransaction() {
    }

    private void startTransaction() {

    }

    private void processRecord(ConsumerRecord<String, StockPrice> record) {
        //SAVE TO DB
    }


}
