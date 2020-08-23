# Lab 6.2: StockPriceConsumer Controlling Consumer Position

Welcome to the session 6 lab 2. The work for this lab is done in `~/kafka-training/lab6.2`.
In this lab, you are going to control consumer position.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/45620559570880b378788bce65848d5f).

## Lab Control Consumer Position

### Offsets and Consumer Position

Consumer position is the topic, partition and offset of the last record per partition consuming. Offset for each record in a partition as a unique identifier record location in the partition.
Consumer position gives offset of next *(highest)* record that it consumes and the position advances automatically for each call to `poll(..)`.

### Consumer committed Position

Consumer committed position is the last offset that has been stored to the Kafka broker if the consumer fails, this allows the consumer to picks up at the last committed position.
Consumer can auto commit offsets ***(enable.auto.commit)*** periodically ***(auto.commit.interval.ms)*** or do commit explicitly using `commitSync()` and `commitAsync()`.

### Consumer Groups

Kafka organizes Consumers into consumer groups. Consumer instances that have the same ***group.id*** are in the same consumer group. Pools of consumers in a group divide work of consuming and processing records. In the consumer groups, processes and threads can run on the same box or run distributed for ***scalability/fault tolerance***.

Kafka shares ***topic partitions*** among all consumers in a consumer group, each partition is assigned to exactly one consumer in a consumer group. ***E.g.*** One topic has six partitions, and a consumer group has two consumer process, each process gets consume three partitions.
If a consumer fails, Kafka reassigned partitions from failed consumer to other consumers in the same consumer group. If new consumer joins, Kafka moves partitions from existing consumers to the new consumer.
A ***Consumer group*** forms a ***single logical subscriber*** made up of multiple consumers. Kafka is a multi-subscriber system, Kafka supports *N* number of ***consumer groups*** for a given topic without duplicating data.

### Partition Reassignment

Consumer partition reassignment in a consumer group happens automatically.
Consumers are notified via ***ConsumerRebalanceListener*** and triggers consumers to finish necessary clean up.
A Consumer can use the API to assign specific partitions using the ***assign***(Collection) method, but using `assign` disables dynamic partition assignment and consumer group coordination.
Dead consumers may see `CommitFailedException` thrown from a call to `commitSync()`. Only active members of consumer group can commit offsets.

### Controlling Consumers Position

You can control consumer position moving to forward or backward. Consumers can re-consume older records or skip to the most recent records.

#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SeekTo.java
#### Kafka Consumer:  SeekTo
```java
package com.cloudurable.kafka.consumer;
public enum SeekTo {
    START, END, LOCATION, NONE
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SeekTo.java` and follow the instructions in the file.

Use ***consumer.seek***(TopicPartition, long) to specify. ***E.g.*** `consumer.seekToBeginning(Collecion)` and `consumer.seekToEnd(Collection)`
***Use Case Time-sensitive record processing:*** Skip to most recent records.
***Use Case Bug Fix:*** Reset position before bug fix and replay log from there.
***Use Case Restore State for Restart or Recovery:*** Consumer initialize position on start-up to whatever is contained in local store and replay missed parts (cache warm-up or replacement in case of failure assumes Kafka retains sufficient history or you are using log compaction).

### Managing Offsets

For the consumer to manage its own offset you just need to do the following:
Set ***enable.auto.commit = false***
Use offset provided with each ConsumerRecord to save your position (partition/offset)
On restart restore consumer position using ***kafkaConsumer.seek(TopicPartition, long)***
Usage like this simplest when the partition assignment is also done manually using ***assign()*** instead of ***subscribe()***.
If using automatic partition assignment, you must handle cases where partition assignments changes.
Pass ***ConsumerRebalanceListener*** instance in call to ***kafkaConsumer.subscribe***(Collection, ConsumerRebalanceListener) and ***kafkaConsumer.subscribe***(Pattern, ConsumerRebalanceListener).
When partitions taken from consumer, commit its offset for partitions by implementing ***ConsumerRebalanceListener.onPartitionsRevoked***(Collection) and when partitions are assigned to consumer, look up offset for new partitions and correctly initialize consumer to that position by implementing ***ConsumerRebalanceListener.onPartitionsAssigned***(Collection).

#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.Consumer
```java
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
...
    private static Consumer<String, StockPrice> createConsumer(final SeekTo seekTo,
                                     final long location) {
        final Properties props = initProperties();

        // Create the consumer using props.
        final Consumer<String, StockPrice> consumer =
                new KafkaConsumer<>(props);

        final ConsumerRebalanceListener consumerRebalanceListener = null;

        consumer.subscribe(Collections.singletonList(
                StockAppConstants.TOPIC), consumerRebalanceListener);
        return consumer;
    }
...
}

```

#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.Consumer
```java
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
...
    public static void main(String... args) throws Exception {

        SeekTo seekTo = SeekTo.NONE; // SeekTo what?
        long location = -1; // Location to seek to if SeekTo.Location
        int readCountStatusUpdate = 100;
        if (args.length >= 1) {
            seekTo = SeekTo.valueOf(args[0].toUpperCase());
            if (seekTo.equals(SeekTo.LOCATION)) {
                location = Long.parseLong(args[1]);
            }
        }
        if (args.length == 3) {
            readCountStatusUpdate = Integer.parseInt(args[2]);
        }
        runConsumer(seekTo, location, readCountStatusUpdate);
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and follow the instructions for the main method.

### Controling Consumers Position Example

#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SeekToConsumerRebalanceListener.java
#### Kafka Consumer:  SeekToConsumerRebalanceListener
```java
package com.cloudurable.kafka.consumer;
import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import java.util.Collection;
public class SeekToConsumerRebalanceListener implements ConsumerRebalanceListener {
    private final Consumer<String, StockPrice> consumer;
    private final SeekTo seekTo; private boolean done;
    private final long location;
    private final long startTime = System.currentTimeMillis();
    public SeekToConsumerRebalanceListener(final Consumer<String, StockPrice> consumer, final SeekTo seekTo,
                                            final long location) {
        this.seekTo = seekTo;
        this.location = location;
        this.consumer = consumer;
    }
    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        if (done) return;
        else if (System.currentTimeMillis() - startTime > 30_000) {
            done = true;
            return;
        }
        switch (seekTo) {
            case END:                   //Seek to end
                consumer.seekToEnd(partitions);
                break;
            case START:                 //Seek to start
                consumer.seekToBeginning(partitions);
                break;
            case LOCATION:              //Seek to a given location
                partitions.forEach(topicPartition ->
                        consumer.seek(topicPartition, location));
                break;
        }
    }
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SeekToConsumerRebalanceListener.java` and follow the instructions in file.


#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.runConsumer
```java
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
...
    private static void runConsumer(final SeekTo seekTo, final long location,
                                    final int readCountStatusUpdate) throws InterruptedException {
        final Map<String, StockPrice> map = new HashMap<>();
        try (final Consumer<String, StockPrice> consumer =
                     createConsumer(seekTo, location)) {
            final int giveUp = 1000; int noRecordsCount = 0; int readCount = 0;
            while (true) {
                final ConsumerRecords<String, StockPrice> consumerRecords =
                        consumer.poll(1000);
                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }
                readCount++;
                consumerRecords.forEach(record -> {
                    map.put(record.key(), record.value());
                });
                if (readCount % readCountStatusUpdate == 0) {
                    displayRecordsStatsAndStocks(map, consumerRecords);
                }
                consumer.commitAsync();
            }
        }
        System.out.println("DONE");
    }
...
}

```

Use SeekTo, Position and ReadCountStatusUpdate

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and follow the instructions for the runConsumer method.


#### ~/kafka-training/lab6.2/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.Consumer
```java
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
...
    private static Consumer<String, StockPrice> createConsumer(final SeekTo seekTo,
                                                               final long location) {
        final Properties props = initProperties();

        // Create the consumer using props.
        final Consumer<String, StockPrice> consumer =
                new KafkaConsumer<>(props);

        // Create SeekToConsumerRebalanceListener and assign it to consumerRebalanceListener
        final ConsumerRebalanceListener consumerRebalanceListener =
                new SeekToConsumerRebalanceListener(consumer, seekTo, location);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(
                StockAppConstants.TOPIC), consumerRebalanceListener);
        return consumer;
    }
...
}

```

Create ***SeekToConsumerRebalanceListener*** passing `SeekTo` and `location`

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and follow the instructions for the createConsumer method.

## ***ACTION*** - RUN ZooKeeper and Brokers if needed.
## ***ACTION*** - RUN SimpleStockPriceConsumer run with moving to start of log
## ***ACTION*** - RUN StockPriceKafkaProducer
## ***ACTION*** - RUN SimpleStockPriceConsumer run with moving to end of log
## ***ACTION*** - RUN SimpleStockPriceConsumer run with moving to a certain location in log

It should all run. Stop consumer and producer when finished.

## Auto offset reset config

What happens when you seek to an invalid location is controlled by consumer-based configuration property:

```
ConsumerConfig.AUTO_OFFSET_RESET_CONFIG = "auto.offset.reset"

Valid values: "latest", "earliest", "none"
```

* latest: automatically reset the offset to the latest offset
* earliest: automatically reset the offset to the earliest offset
* none: throw an exception to the consumer if no previous offset is found for the consumer's group anything else: throw exception to the consumer.

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
