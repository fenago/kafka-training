# Lab 6.7: Implementing a priority queue with consumer.assign()

Welcome to the session 6 lab 7. The work for this lab is done in `~/kafka-training/lab6.7`.
In this lab, you are going to implement a priority queue with `consumer.assign()`.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/81a66e0f9822e9e74660deec10640d27).


## Lab Using consumer.assign to implement a priority queue

In this lab, you will implement a priority processing queue.
You will use `consumer.partitionsFor(TOPIC)` to get a list of partitions.
Usage like this simplest when the partition assignment is also done manually using `assign()` instead of `subscribe()`.
Use `assign()`, pass a `TopicPartition` from the consumer worker.
Use the `Partitioner` from an earlier example for Producer so only important stocks get sent to the important partition.

## Using partitionsFor() for Priority Queue

#### ~/kafka-training/lab6.7/src/main/java/com/cloudurable/kafka/consumer/ConsumerMain.java
#### Kafka Consumer:  ConsumerMain.main
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

import static com.cloudurable.kafka.StockAppConstants.TOPIC;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConsumerMain {
    ...
    public static void main(String... args) throws Exception {
        final AtomicBoolean stopAll = new AtomicBoolean();
        final Consumer<String, StockPrice> consumer = createConsumer();

        //Get the partitions
        final List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC);

        final int threadCount = partitionInfos.size();
        final int numWorkers = 5;
        final ExecutorService executorService = newFixedThreadPool(threadCount);

        IntStream.range(0, threadCount).forEach(index -> {
            final PartitionInfo partitionInfo = partitionInfos.get(index);
            final boolean leader = partitionInfo.partition() == partitionInfos.size() -1;
            final int workerCount = leader ? numWorkers * 3 : numWorkers;
            final StockPriceConsumerRunnable stockPriceConsumer =
                    new StockPriceConsumerRunnable(partitionInfo, createConsumer(),
                            readCountStatusUpdate: 10, index, stopAll, workerCount);
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
...
}

```

Notice that the index is the topic partition. Num threads are the partition count, and the priority partition gets extra workers.

## Using assign() for Priority Queue

#### ~/kafka-training/lab6.7/src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java
#### Kafka Consumer:  StockPriceConsumerRunnable.runConsumer
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
    private void runConsumer() throws Exception {
        //Assign a partition
        consumer.assign(Collections.singleton(topicPartition));
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
    ...
}

```

## Lab Work

Use the slides for Session 6 as a guide.

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/ConsumerMain.java` and follow the instructions in the file.
## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/StockPriceConsumerRunnable.java` and follow the instructions in the file.
## ***ACTION*** - RECREATE the topic with five partitions (HINT: bin/create-topic.sh) and use 5 partitions.


## ***ACTION*** - RUN ZooKeeper and Brokers if needed.
## ***ACTION*** - RUN ConsumerMain from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumers and producer

## Expected behavior
It should run and should get messages like this:

#### Expected output

```sh
New ConsumerRecords par count 1 count 153, max offset
ticker IBM price 66.59 Thread 4
ticker UBER price 241.94 Thread 4

New ConsumerRecords par count 1 count 220, max offset
ticker ABC price 95.85 Thread 2
ticker BBB price 53.36 Thread 2
ticker FFF price 70.34 Thread 2

New ConsumerRecords par count 1 count 318, max offset
ticker GOOG price 458.44 Thread 0
ticker DDD price 68.38 Thread 0
ticker SUN price 91.90 Thread 0
ticker INEL price 65.94 Thread 0

New ConsumerRecords par count 1 count 364, max offset
ticker AAA price 66.53 Thread 1
ticker DEF price 65.94 Thread 1
ticker EEE price 70.34 Thread 1
ticker XYZ price 65.94 Thread 1

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
