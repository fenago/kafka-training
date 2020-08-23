# Lab 6.4: StockPriceConsumer Exactly Once Consumer Messaging Semantics

Welcome to the session 6 lab 4. The work for this lab is done in `~/kafka-training/lab6.4`.
In this lab, you are going to implement ***Exactly Once*** messaging semantics.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/dfec9a329c617f69e2e5835b218b01b9).


## Lab Exactly Once Semantics


To implement Exactly-Once semantics, you have to control and store the offsets
for the partitions with the output of your consumer operation.
You then have to read the stored positions when your consumer is assigned partitions to consume.

Remember that consumers do not have to use Kafka's built-in offset storage, and to implement ***exactly once messaging semantics***, you will need to read the offsets from stable storage. In this example, we use a JDBC database.

You will need to store offsets with processed record output to make it “exactly once” message consumption.

You will store the output of record consumption in an RDBMS with the offset, and partition.
This approach allows committing both processed record output and location (partition/offset of record) in a single
transaction thus implementing “exactly once” messaging.

### StockPriceRecord

StockPriceRecord holds offset and partition info

#### ~/kafka-training/lab6.4/src/main/java/com/cloudurable/kafka/consumer/StockPriceRecord.java
#### Kafka Consumer:  StockPriceRecord
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public class StockPriceRecord  {

    private final String topic;
    private final int partition;
    private final long offset;
    private final String name;
    private final int dollars;
    private final int cents;
    private final boolean saved;
    private final TopicPartition topicPartition;

    public StockPriceRecord(String topic, int partition, long offset,
                            String name, int dollars, int cents, boolean saved) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.name = name;
        this.dollars = dollars;
        this.cents = cents;
        this.saved = saved;
        topicPartition = new TopicPartition(topic, partition);
    }
...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/StockPriceRecord.java` follow the instructions in the file.

### DatabaseUtilities

The `DatabaseUtilities` class saves Topic, Offset, Partition data in the Database.

#### ~/kafka-training/lab6.4/src/main/java/com/cloudurable/kafka/consumer/DatabaseUtilities.java
#### Kafka Consumer:  DatabaseUtilities.saveStockPrice
```java
package com.cloudurable.kafka.consumer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtilities {
...
    public static void saveStockPrice(final StockPriceRecord stockRecord,
                                      final Connection connection) throws SQLException {

        final PreparedStatement preparedStatement = getUpsertPreparedStatement(
                stockRecord.getName(), connection);



        //Save partition, offset and topic in database.
        preparedStatement.setLong( parameterindex:1, stockRecord.getOffset());
        preparedStatement.setLong( parameterindex:2, stockRecord.getPartition());
        preparedStatement.setString( parameterindex:3, stockRecord.getTopic());

        //Save stock price, name, dollars, and cents into database.
        preparedStatement.setInt( parameterindex:4, stockRecord.getDollars());
        preparedStatement.setInt( parameterindex:5, stockRecord.getCents());
        preparedStatement.setString( parameterindex:6, stockRecord.getName());

        //Save the record with offset, partition, and topic.
        preparedStatement.execute();

    }
...
}

```

To get exactly once, you need to save the offset and partition with the output of the consumer process.

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/DatabaseUtilities.java` follow the instructions in the file.

### SimpleStockPriceConsumer

"Exactly-Once" - Delivery Semantics

#### ~/kafka-training/lab6.4/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.pollRecordsAndProcess
```java
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

public class SimpleStockPriceConsumer
{
...
    private static void pollRecordsAndProcess(
            int readCountStatusUpdate,
            Consumer<String, StockPrice> consumer,
            Map<String, StockPriceRecord> currentStocks, int readCount) throws Exception {

        final ConsumerRecords<String, StockPrice> consumerRecords =
                consumer.poll(1000);

        if (consumerRecords.count() == 0) return;


        //Get rid of duplicates and keep only the latest record.
        consumerRecords.forEach(record -> currentStocks.put(record.key(),
                new StockPriceRecord(record.value(), saved: false, record)));

        final Connection connection = getConnection();
        try {
            startJdbcTransaction(connection)            //Start DB Transaction
            for (StockPriceRecord stockRecordPair : currentStocks.values()) {
                if (!stockRecordPair.isSaved()) {
                            //Save the record
                            //with partition/offset to DB.
                    saveStockPrice(stockRecordPair, connection);
                    //Mark the record as saved
                    currentStocks.put(stockRecordPair.getName(), new
                            StockPriceRecord(stockRecordPair, saved: true));
                }
            }
            connection.commit();            //Commit DB Transaction
            consumer.commitSync();            //Commit the Kafka offset
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
...
}

```

Try to commit the DB transaction and if it succeeds, commit the offset position.

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` follow the instructions in the file to commit database transaction and Kafka log.

## Initializing and saving offsets from ConsumerRebalanceListener

If implementing “exactly once” message semantics, then you have to manage offset positioning
with a `ConsumerRebalanceListener` which gets notified when partitions are assigned or taken away from
a consumer.

You will implement a `ConsumerRebalanceListener` and then
pass the `ConsumerRebalanceListener` instance in call to
`kafkaConsumer.subscribe(Collection, ConsumerRebalanceListener)`.

`ConsumerRebalanceListener` is notified when partitions get taken away from a consumer,
so the consumer can commit its offset for partitions by implementing
`ConsumerRebalanceListener.onPartitionsRevoked(Collection)`.

When partitions get assigned to a consumer, you will need to look up the offset
in a database for new partitions and correctly initialize consumer to that position
by implementing `ConsumerRebalanceListener.onPartitionsAssigned(Collection)`.

### SeekToLatestRecordsConsumerRebalanceListener

"Exactly-Once" - Delivery Semantics

#### ~/kafka-training/lab6.4/src/main/java/com/cloudurable/kafka/consumer/SeekToLatestRecordsConsumerRebalanceListener.java
#### Kafka Consumer:  SeekToLatestRecordsConsumerRebalanceListener.onPartitionsAssigned
```java
package com.cloudurable.kafka.consumer;

import com.cloudurable.kafka.model.StockPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class SeekToLatestRecordsConsumerRebalanceListener
                                implements ConsumeRebalanceListener{
    private final Consumer<String, StockPrice> consumer;
    private static final Logger logger = getLogger(SimpleStockPriceConsumer.class);

    public SeekToLatestRecordsConsumerRebalanceListener(
            final Consumer<String, StockPrice> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        final Map<TopicPartition, Long> maxOffsets = getMaxOffsetsFromDatabase();
        maxOffsets.entrySet().forEach(
                entry -> partitions.forEach(topicPartition -> {
                    if (entry.getKey().equals(topicPartition)) {
                        long maxOffset = entry.getValue();
                        //Call to consumer.seek to move to the partition.
                        consumer.seek(topicPartition, offset: maxOffset + 1);
                        displaySeekInfo(topicPartition, maxOffset);
                    }
                }));
    }

    ...

    private Map<TopicPartition, Long> getMaxOffsetsFromDatabase() {
        final List<StockPriceRecord> records = DatabaseUtilities.readDB();
        final Map<TopicPartition, Long> maxOffsets = new HashMap<>();
        records.forEach(stockPriceRecord -> {
            final Long offset = maxOffsets.getOrDefault(stockPriceRecord.getTopicPartition(),
                    defaultValue: -1L);
            if (stockPriceRecord.getOffset() > offset) {
                maxOffsets.put(stockPriceRecord.getTopicPartition(),
                        stockPriceRecord.getOffset());
            }
        });
        return maxOffsets;
    }
...

```

We load a map of max offset per TopicPartition from the database. We could (should) use SQL, but for this example, we just use a map and iterate through the current stock price records looking for max. The `maxOffsets` key is TopicPartition and value is the max offset for that partition.
Then we seek to that position with consumer.seek

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SeekToLatestRecordsConsumerRebalanceListener.java` follow the instructions in the file.

### SimpleStockPriceConsumer

Subscribe to this topic using SeekToLatestRecordsConsumerRebalanceListener

#### ~/kafka-training/lab6.4/src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java
#### Kafka Consumer:  SimpleStockPriceConsumer.runConsumer
```java
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

public class SimpleStockPriceConsumer
{
...

    private static void runConsumer(final int readCountStatusUpdate) throws InterruptedException {
        final Map<String, StockPriceRecord> map = new HashMap<>();


        try (final Consumer<String, StockPrice> consumer = createConsumer()) {

            consumer.subscribe(Collections.singletonList(StockAppConstants.TOPIC),
                    new SeekToLatestRecordsConsumerRebalanceListener(consumer));

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

...
}

```

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/SimpleStockPriceConsumer.java` method and follow the instructions to subscribe to topic using SeekToLatestRecordsConsumerRebalanceListener.

## ***ACTION*** - RUN ZooKeeper and Brokers if needed.
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE
## ***ACTION*** - RUN StockPriceKafkaProducer from IDE
## ***ACTION*** - OBSERVE and then STOP consumer and producer

## Expected behavior
You should see offset messages from `SeekToLatestRecordsConsumerRebalanceListener`
in the log for the consumer.

## ***ACTION*** - STOP SimpleStockPriceConsumer from IDE (while you leave StockPriceKafkaProducer for 30 seconds)
## ***ACTION*** - RUN SimpleStockPriceConsumer from IDE


## Expected behavior
Again, you should see offset messages from `SeekToLatestRecordsConsumerRebalanceListener` in the log for the consumer.

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
