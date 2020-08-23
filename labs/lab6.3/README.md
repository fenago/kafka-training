# Lab 6.3: StockPriceConsumer At Most Once and At Least Once

Welcome to the session 6 lab 3. The work for this lab is done in `~/kafka-training/lab6.3`.
In this lab, you are going to implement At-Most-Once and At-Least-Once message semantics from the
consumer perspective.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/8411962423cdda40e09190646977b959).

## Lab At-Most-Once and At-Least-Once Semantics

### Consumer Alive Detection

Consumers join consumer group after `subscribe` and then `poll()` is called.
Automatically, a consumer sends periodic heartbeats to Kafka brokers server.
If consumer crashes or is unable to send heartbeats for a duration of ***session.timeout.ms***, then the consumer is deemed dead, and its partitions reassigned.

### Manual Partition Assignment

Instead of subscribing to the topic using subscribe, you can call ***assign(Collection)*** with the full topic partition list
```
String topic = "log-replication";
TopicPartition part0 = new TopicPartition(topic, 0);
TopicPartition part1 = new TopicPartition(topic, 1);
consumer.assign(Arrays.asList(part0, part1);
```
Using consumer as before with ***poll()***.
Manual Partition assignment negates the use of group coordination and auto consumer failover. Each consumer acts independently even if in a consumer group (use unique group id to avoid confusion).
You have to use ***assign()*** or ***subscribe()***, but not both.
Use ***subscribe()*** to allow Kafka to manage failover and load balancing with consumer groups. Use ***assign()*** if you want to work with partitions exact.

### Consumer Alive if Polling

Calling ***poll()*** marks consumer as alive. If consumer continues to call ***poll()***, then consumer is alive and in consumer group and gets messages for partitions assigned (has to call before every ***max.poll.interval.ms*** interval), If not calling ***poll()***, even if consumer is sending heartbeats, consumer is still considered dead.
Processing of records from ***poll*** has to be faster than ***max.poll.interval.ms*** interval or your consumer could be marked dead!
The ***max.poll.records*** is used to limit total records returned from a `poll` method call and makes it easier to predict max time to process the records on each poll interval.

### Message Delivery Semantics

There are three message delivery semantics: at most once, at least once and exactly once.

At most once is messages may be lost but never redelivered. At least once is messages are never lost but may be redelivered. Exactly once is each message is delivered once and only once. Exactly once is preferred but more expensive, and requires more bookkeeping for the producer and consumer.

### "At-Least-Once" - Delivery Semantics

#### ~/kafka-training/lab6.3/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.pollRecordsAndProcess
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
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPrice> map, int readCount) {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(1000);

        try {
            startTransaction();         //Start DB Transaction
            processRecords(map, consumerRecords);    //Process the records
            consumer.commitSync();
            commitTransaction();        //Commit DB Transaction
        } catch (CommitFailedException ex) {
            logger.error("Failed to commit sync to log", ex);
            rollbackTransaction();      //Rollback Transaction
        } catch (DatabaseException dte) {
            logger.error("Failed to write to DB", dte);
            rollbackTransaction();      //Rollback Transaction
        }
        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(map, consumerRecords);
        }
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and implement At-Least-Once Semantics
## ***ACTION*** - RUN ZooKeeper and Brokers if needed.
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumer and producer

### "At-Most-Once" - Delivery Semantics

#### ~/kafka-training/lab6.3/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.pollRecordsAndProcess
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
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPrice> map, int readCount) {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(1000);

        try {

            startTransaction();         //Start DB Transaction
            consumer.commitSync();        //Commit the Kafka offset
            processRecords(map, consumerRecords);    //Process the records
            commitTransaction();        //Commit DB Transaction
        } catch (CommitFailedException ex) {
            logger.error("Failed to commit sync to log", ex);
            rollbackTransaction();      //Rollback Transaction
        } catch (DatabaseException dte) {
            logger.error("Failed to write to DB", dte);
            rollbackTransaction();      //Rollback Transaction
        }
        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(map, consumerRecords);
        }
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and implement At-Most-Once Semantics
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumer and producer

### Fine Grained "At-Least-Once"

#### ~/kafka-training/lab6.3/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.pollRecordsAndProcess
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
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPrice> map, int readCount)
            consumerRecords.forEach(record -> {
                try {
                    startTransaction();         //Start DB Transaction
                    //Commit Kafka at exact location for record, and only this record.
                    final TopicPartition recordTopicPartition =
                        new TopicPartition(record.topic(), record.partition());
                    final Map<TopicPartition, OffsetAndMetadata> commitMap =
                        Collections.singletonMap(recordTopicPartition,
                        new OffsetAndMetadata( offset: record.offset() + 1));
                    consumer.commitSync(commitMap); //Kafka Commit
                    processRecords(record);            //Process the record
                    commitTransaction();        //Commit DB Transaction
                } catch (CommitFailedException ex) {
                    logger.error("Failed to commit sync to log", ex);
                    rollbackTransaction();      //Rollback Transaction
                } catch (DatabaseException dte) {
                    logger.error("Failed to write to DB", dte);
                    rollbackTransaction();      //Rollback Transaction
                }
            });
        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(map, consumerRecords);
        }
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and implement fine-grained At-Most-Once Semantics
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumer and producer

### Fine Grained "At-Most-Once"

#### ~/kafka-training/lab6.3/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.pollRecordsAndProcess
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
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPrice> map, int readCount)
            consumerRecords.forEach(record -> {
                try {
                    startTransaction();         //Start DB Transaction
                    processRecords(record);            //Process the record
                            //Commit Kafka at exact location for the record, and only this record.
                    final TopicPartition recordTopicPartition =
                        new TopicPartition(record.topic(), record.partition());
                    final Map<TopicPartition, OffsetAndMetadata> commitMap =
                        Collections.singletonMap(recordTopicPartition,
                        new OffsetAndMetadata( offset: record.offset() + 1));
                    consumer.commitSync(commitMap); //Kafka Commit
                    commitTransaction();        //Commit DB Transaction
                } catch (CommitFailedException ex) {
                    logger.error("Failed to commit sync to log", ex);
                    rollbackTransaction();      //Rollback Transaction
                } catch (DatabaseException dte) {
                    logger.error("Failed to write to DB", dte);
                    rollbackTransaction();      //Rollback Transaction
                }
            });
        if (readCount % readCountStatusUpdate == 0) {
            displayRecordsStatsAndStocks(map, consumerRecords);
        }
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` and implement fine-grained At-Least-Once Semantics
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumer and producer

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
