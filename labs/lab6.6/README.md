# Lab 6.6: Consumer with many threads

Welcome to the session 6 lab 6. The work for this lab is done in `~/kafka-training/lab6.6`.
In this lab, you are going to implement consumers with many threads.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/041c2c546e513a3481289ca16fe11cd4).


## Lab Consumer with many threads

Recall that unlike Kafka producers, Kafka consumers are not thread-safe.

All network I/O happens in a thread of the application making calls.  Kafka Consumers
manage buffers, and connections state that threads can't share.

Remember only exception thread-safe method that the consumer has is `consumer.wakeup()`.
The wakeup() method forces the consumer to throw a WakeupException on any thread the
consumer client is blocking.  You can use this to shut down a consumer from another thread.
The solution and lab use wakeup. This is an easter egg.


## Consumer with many threads

Decouple Consumption and Processing: One or more consumer threads that consume from Kafka
and hands off ConsumerRecords instances to a thread pool where a worker thread can process it.
This approach uses a blocking queue per topic partition to commit offsets to Kafka.
This method is useful if per record processing is time-consuming.

An advantage is an option allows independently scaling consumers count and processors count.
Processor threads are independent of topic partition count is also a significant advantage.

The problem with this approach is that guaranteeing order across processors requires care as threads execute independently and a later record could be processed before an
earlier record and then you have to do consumer commits somehow with this out of order offsets.

How do you commit the position unless there is some order? You have to provide the ordering.
(We use `ConcurrentHashMap` of `BlockingQueues` where the topic partition is the key (`TopicPartition`).)

### One Consumer with Worker Threads

#### ~/kafka-training/lab6.6/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  StockPriceConsumerRunnable
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;

public class StockPriceConsumerRunnable implements Runnable {
    private static final Logger logger =
            LoggerFactory.getLogger(StockPriceConsumerRunnable.class);

    private final Consumer<String, StockPrice> consumer;
    private final int readCountStatusUpdate;
    private final int threadIndex;
    private final AtomicBoolean stopAll;
    private boolean running = true;

    //Store blocking queue by TopicPartition
    Map<TopicPartition, BlockingQueue<ConsumerRecord>>
    commitQueueMap = new ConcurrentHashMap<>();

    //Worker pool.
    private final ExecutorService threadPool;
    ...
}

```

Map of queues per TopicPartition and threadPool;

## StockPriceConsumerRunnable is still Runnable, but now it has many threads

You will need to add a worker `threadPool` to the `StockPriceConsumerRunnable`.
The `StockPriceConsumerRunnable` uses a map of blocking queues per TopicPartition to manage
sending offsets to Kafka.

### Consumer with Worker Threads: Use Worker

#### ~/kafka-training/lab6.6/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  StockPriceConsumerRunnable.pollRecordsAndProcess
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;

public class StockPriceConsumerRunnable implements Runnable {
...
    private void pollRecordsAndProcess(
            final Map<String, StockPrice> currentStocks,
            final int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
            consumer.poll(timeout: 100);

        if (consumerRecords.count() == 0) {
            if (stopAll.get()) this.setRunning(false);
            return;
        }

        consumer.Records.forEach(record ->
                currentStocks.put(record.key(),
                        new StockPriceRecord(record.value(), saved: true, record)
                ));

        threadPool.execute(() ->
                processRecords(currentStocks, consumerRecords));

        processCommits();

        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(currentStocks, consumerRecords);
        }
    }
...
}

```

Process the records async, and calls method `processCommits` will send offsets to Kafka.

### Consumer with Worker Threads: processCommits

#### ~/kafka-training/lab6.6/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  StockPriceConsumerRunnable.processCommits
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;

public class StockPriceConsumerRunnable implements Runnable {
...
    private void processCommits() {

        commitQueueMap.entrySet().forEach(queueEntry -> {
            final BlockingQueue<ConsumerRecord> queue = queueEntry.getValue();
            final TopicPartition topicPartition = queueEntry.getKey();

            ConsumerRecord consumerRecord = queue.poll();
            ConsumerRecord highestOffset = consumerRecord;

            while (consumerRecord != null) {
                if (consumerRecord.offset() > highestOffset.offset()) {
                    highestOffset = consumerRecord;
                }
                consumerRecord = queue.poll();
            }


            if (highestOffset != null) {
                logger.info(String.format("Sending commit %s %d",
                        topicPartition, highestOffset.offset()));
                try {
                    consumer.commitSync(Collections.singletonMap(topicPartition,
                            new OffsetAndMetadata(highestOffset.offset())));
                } catch (CommitFailedException cfe) {
                    logger.error("Failed to commit record", cfe);
                }
            }
        });
    }
...
}

```

Track the highest offset per TopicPartition, then call `consumer.commitSync` to save offset in Kafka per partition

### Submit Records to Commit Queues per Partition

#### ~/kafka-training/lab6.6/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  StockPriceConsumerRunnable
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cloudurable.kafka.StockAppConstants.TOPIC;

public class StockPriceConsumerRunnable implements Runnable {
...
    private void processRecords(final Map<String, StockPrice> currentStocks,
                        final ConsumerRecords<String, StockPrice> consumerRecords) {

        consumerRecords.forEach(record ->
                currentStocks.put(record.key(), record.value()));

        consumerRecords.forEach(record -> {

            try {
                startTransaction();           //Start DB Transaction
                processRecord(record);
                commitTransaction();          //Commit DB Transaction
                commitRecordOffsetToKafka(record); //Send record to commit queue for Kafka
            } catch (DatabaseException dbe) {
                rollbackTransaction();
            }
        });

    }
    ...
    private void commitRecordOffsetToKafka(ConsumerRecord<String, StockPrice> record) {
        final TopicPartition topicPartition =
                new TopicPartition(record.topic(), record.partition());
        final BlockingQueue<ConsumerRecord> queue = commitQueueMap.computeIfAbsent (
                topicPartition,
                k -> new LinkedTransferQueue<>());
        queue.add(record);
    }
...
}

```

The method ***processRecord()***, if successful send a record to commit queue where it can be processed by `processCommits`.
The method ***commitRecordOffsetToKafka()***, commits the record to its topicPartition Queue were it can be processed later by processCommits.

## ConsumerMain

`ConsumerMain` now uses `wakeup` to stop consumer threads gracefully.
Check this out. `ConsumerMain` also passes the number of worker threads that each
`StockPriceConsumerRunnable` runs.

#### ~/kafka-training/lab6.6/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  ConsumerMain
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.StockAppConstants;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConsumerMain {
    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerMain.class);


    private static Consumer<String, StockPrice> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                StockAppConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StockDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        return new KafkaConsumer<>(props);
    }

    public static void main(String... args) throws Exception {
        final int threadCount = 5;
        final int workerThreads = 5;
        final ExecutorService executorService = newFixedThreadPool(threadCount);
        final AtomicBoolean stopAll = new AtomicBoolean();
        final List<Consumer> consumerList = new ArrayList<Consumer>(threadCount);
        IntStream.range(0, threadCount).forEach(index -> {
            final Consumer<String, StockPrice> consumer = createConsumer();
            final StockPriceConsumerRunnable stockPriceConsumer =
                    new StockPriceConsumerRunnable(consumer,
                            1000, index, stopAll, workerThreads);
            consumerList.add(consumer);
            executorService.submit(stockPriceConsumer);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping app");
            stopAll.set(true);
            sleep();
            consumerList.forEach(Consumer::wakeup);
            executorService.shutdown();
            try {
                executorService.awaitTermination(5_000, TimeUnit.MILLISECONDS);
                if (!executorService.isShutdown())
                    executorService.shutdownNow();
            } catch (InterruptedException e) {
                logger.warn("shutting down", e);
            }
            sleep();
            consumerList.forEach(Consumer::close);
        }));
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

}

```

## Lab Work

Use the slides for Session 6 as a guide.

## ***ACTION*** - EDIT `com.cloudurable.kafka.consumer.StockPriceConsumerRunnable` and follow the instructions in the file.
## ***ACTION*** - EDIT `com.cloudurable.kafka.consumer.ConsumerMain` and follow the instructions in the file.
## ***ACTION*** - RECREATE the topic with more partitions (HINT: bin/create-topic.sh).


## ***ACTION*** - RUN ZooKeeper and Brokers if needed.
## ***ACTION*** - RUN ConsumerMain from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumers and producer

## Expected behavior
It should run and should get messages like this:

#### Expected output

```sh
15:57:34.945 [pool-1-thread-2] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-9 320
15:57:34.947 [pool-1-thread-5] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-20 161
15:57:34.947 [pool-1-thread-3] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-12 317
15:57:34.947 [pool-1-thread-4] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-18 320
15:57:34.950 [pool-1-thread-5] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-24 305
15:57:34.952 [pool-1-thread-5] INFO  c.c.k.c.StockPriceConsumerRunnable -
Sending commit stock-prices-21 489
```

## Try the following

Try using different worker pool sizes and different consumer thread pool sizes.
Try adding a small wait for the processing. Try 10ms.


It should all run. Stop consumer and producer when finished.

____

# Kafka Tutorial

This comprehensive *Kafka tutorial* covers Kafka architecture and design. The *Kafka tutorial* has example Java Kafka producers and Kafka consumers. The *Kafka tutorial* also covers Avro and Schema Registry.

[Complete Kafka Tutorial: Architecture, Design, DevOps and Java Examples.](http://cloudurable.com/blog/kafka-tutorial-kafka-producer-advanced-java-examples/index.html "Comprehensive Apache Kafka tutorial and training series")


* [Kafka Tutorial Part 1: What is Kafka?](http://cloudurable.com/blog/what-is-kafka/index.html "This Kafka tutorial describes what Kafka is. Kafka is a fast, scalable, durable, and fault-tolerant publish-subscribe messaging system, Kafka is used in use cases where JMS, RabbitMQ, and AMQP may not even be considered due to volume and responsiveness. It covers the impact of Kafka, who uses it and why it is important")
* [Kafka Tutorial Part 2: Kafka Architecture](http://cloudurable.com/blog/kafka-architecture/index.html "This Kafka tutorial discusses the structure of Kafka. Kafka consists of Records, Topics, Consumers, Producers, Brokers, Logs, Partitions, and Clusters. Records can have key, value and timestamp. Kafka Records are immutable. A Kafka Topic is a stream of records - \"/orders\", \"/user-signups\". You can think of a Topic as a feed name. It covers the structure of and purpose of topics, log, partition, segments, brokers, producers, and consumers")
* [Kafka Tutorial Part 3: Kafka Topic Architecture](http://cloudurable.com/blog/kafka-architecture-topics/index.html "This Kafka tutorial covers some lower level details of Kafka topic architecture. It is a continuation of the Kafka Architecture article. This article covers Kafka Topic's Architecture with a discussion of how partitions are used for fail-over and parallel processing.")
* [Kafka Tutorial Part 4: Kafka Consumer Architecture](http://cloudurable.com/blog/kafka-architecture-consumers/index.html "This Kafka tutorial covers Kafka Consumer Architecture with a discussion consumer groups and how record processing is shared among a consumer group as well as failover for Kafka consumers.")
* [Kafka Tutorial Part 5: Kafka Producer Architecture](http://cloudurable.com/blog/kafka-architecture-producers/index.html "This Kafka tutorial covers Kafka Producer Architecture with a discussion of how a partition is chosen, producer cadence, and partitioning strategies.")
* [Kafka Tutorial Part 6: Using Kafka from the command line](http://cloudurable.com/blog/kafka-tutorial-kafka-from-command-line/index.html "This Kafka tutorial covers using Kafka from the command line starts up ZooKeeper, and Kafka and then uses Kafka command line tools to create a topic, produce some messages and consume them.")
* [Kafka Tutorial Part 7: Kafka Broker Failover and Consumer Failover](http://cloudurable.com/blog/kafka-tutorial-kafka-failover-kafka-cluster/index.html "This Kafka tutorial covers creating a replicated topic. Then demonstrates Kafka consumer failover and Kafka broker failover. Also demonstrates load balancing Kafka consumers. Article shows how, with many groups, Kafka acts like a Publish/Subscribe message broker. But, when we put all of our consumers in the same group, Kafka will load share the messages to the consumers in the same group like a queue.")
* [Kafka Tutorial Part 8: Kafka Ecosystem](http://cloudurable.com/blog/kafka-ecosystem/index.html "This Kafka tutorial covers Kafka ecosystem: Kafka Core, Kafka Streams, Kafka Connect, Kafka REST Proxy, and the Schema Registry")
* [Kafka Tutorial Part 9: Kafka Low-Level Design](http://cloudurable.com/blog/kafka-architecture-low-level/index.html "This Kafka tutorial is a discussion of Kafka Architecture regarding low-level design details for scale failover, and recovery.")
* [Kafka Tutorial Part 10: Kafka Log Compaction Architecture](http://cloudurable.com/blog/kafka-architecture-log-compaction/index.html "This Kafka tutorial covers Kafka log compaction. Kafka can delete older records based on time or size of a log. Kafka also supports log compaction for record key compaction. Log compaction means that Kafka will keep the latest version of a record and delete the older versions during a log compaction.")
* [Kafka Tutorial Part 11: Writing a Kafka Producer example in Java](http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html "This Kafka tutorial covers creating a Kafka Producer in Java and shows a Java Kafka Producer Example")
* [Kafka Tutorial Part 12: Writing a Kafka Consumer example in Java](http://cloudurable.com/blog/kafka-tutorial-kafka-consumer/index.html "This Kafka tutorial covers creating a Kafka Consumer in Java and shows a Java Kafka Consumer Example")
* [Kafka Tutorial Part 13: Writing Advanced Kafka Producer Java examples](http://cloudurable.com/blog/kafka-tutorial-kafka-producer-advanced-java-examples/index.html "This tutorial covers advanced producer topics like custom serializers, ProducerInterceptors, custom Partitioners, timeout, record batching & linger, and compression.")
* Kafka Tutorial 14: Writing Advanced Kafka Consumer Java examples
* [Kafka Tutorial Part 15: Kafka and Avro](http://cloudurable.com/blog/avro/index.html "This Kafka tutorial covers Avro data format, defining schemas, using schemas for Big Data and Data Streaming Architectures with an emphasis on Kafka")
* [Kafka Tutorial Part 16: Kafka and Schema Registry](http://cloudurable.com/blog/kafka-avro-schema-registry/index.html "This Kafka tutorial covers Kafka Avro serialization and operations of the Schema Registry. Also covers using Avro Schema Evolution with the Schema Registry")
* [Kafka Tutorial](http://cloudurable.com/ppt/kafka-tutorial-cloudruable-v2.pdf "PDF slides for a Kafka Tutorial")
____



<br />

#### About Cloudurable
We hope you enjoyed this article. Please provide [feedback](http://cloudurable.com/contact/index.html).
Cloudurable provides [Kafka training](http://cloudurable.com/kafka-training/index.html "Apache Kafka Training Course, Instructor led, onsite training"), [Kafka consulting](http://cloudurable.com/kafka-aws-consulting/index.html), [Kafka support](http://cloudurable.com/subscription_support/index.html) and helps [setting up Kafka clusters in AWS](http://cloudurable.com/services/index.html).
