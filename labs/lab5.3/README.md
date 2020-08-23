# Lab 5.3: Configuring Producer Durability

Welcome to the session 5 lab 3. The work for this lab is done in `~/kafka-training/lab5.3`.
In this lab, you are going to set up producer durability for our advanced producer.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/87b1646895502d9c098a0e5610c8e622).



## Lab Configuring Producer Durability

In this lab we will configure producer durability.

## Set default acks to all


## ***ACTION*** EDIT StockPriceKafkaProducer and set Producer config acks to all (this is the default).
This means that all ISRs in-sync replicas have to respond for producer write to go through.

#### StockPriceKafkaProducer.java
```java
public class StockPriceKafkaProducer {

    private static Producer<String, StockPrice>
                                    createProducer() {
        final Properties props = new Properties();
        ...

        //Set number of acknowledgments - acks - default is all
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return new KafkaProducer<>(props);
    }
}
```

Notice we set the Producer config property "acks" (ProducerConfig.ACKS_CONFIG) to all.


### Kafka Broker Config for Min.Insync.Replicas

Next let's set the the min.insync.replicas to three. This will force at least three  in-sync replicas (ISRs) have 
to respond for our producer to get an ack from the Kafka Broker.
NOTE: We have three brokers in this lab, thus all three have to be up for the Producer to work.

Ensure min.insync.replicas is set to three in all of the broker config files (server-0.properties, server-1.properties
 and server-2.properties).

## ***ACTION*** EDIT config/server-0.properties and min.insync.replicas=3
## ***ACTION*** EDIT config/server-1.properties and min.insync.replicas=3
## ***ACTION*** EDIT config/server-2.properties and min.insync.replicas=3

###  Run this lab. Run it. Run Servers. Run Producer. Kill 1 Broker.

If not already, startup ZooKeeper. Now startup three Kafka brokers (or ensure they are running)
using scripts described earlier.
From the IDE run StockPriceKafkaProducer class. From the terminal kill one of the Kafka Brokers.
Now look at the logs for the StockPriceKafkaProducer, you should see
Caused by: org.apache.kafka.common.errors.NotEnoughReplicasException.
Note that the Messages are rejected since there are fewer in-sync replicas than required.
Repeat this with only 2 min.insync.replicas set. (Change config and restart brokers and restart producer).
Observe the behavior of using 2 for min.insync.replicas vs. three.

## ***ACTION*** RUN brokers and zookeeper if needed.
## ***ACTION*** RUN StockPriceKafkaProducer from the IDE
## ***ACTION*** KILL 1 of the Kafka Brokers
## ***ACTION*** OBSERVE Producer terminal messages
## ***ACTION*** EDIT config/server-0.properties and min.insync.replicas=2
## ***ACTION*** EDIT config/server-1.properties and min.insync.replicas=2
## ***ACTION*** EDIT config/server-2.properties and min.insync.replicas=2
## ***ACTION*** RESTART brokers
## ***ACTION*** KILL 1 of the Kafka Brokers
## ***ACTION*** OBSERVE Producer terminal messages

### Why did the send fail?

The producer config ProducerConfig.ACKS_CONFIG (acks config for producer) was set to “all”.
This settings expects leader to only give successful ack after all followers ack the send was written to their log.
The Broker config min.insync.replicas set to 3.
At least three in-sync replicas must respond before send is considered successful.
Since we took one broker out and only had three to begin with, it forces the send to fail since the send can not 
get three acks from ISRs.

### Modify durability to leader only

Change StockPriceKafkaProducer acks config to 1 `props.put(ProducerConfig.ACKS_CONFIG, “1"`, i.e., leader 
sends ack after write to log.
From the IDE run StockPriceKafkaProducer again.
From the terminal kill one of the Kafka Brokers.
Notice that the StockPriceKafkaProducer now runs normally.

## ***ACTION*** EDIT StockPriceKafkaProducer and set Producer config acks to "1".
## ***ACTION*** RUN all brokers and zookeeper if needed.
## ***ACTION*** RUN StockPriceKafkaProducer from the IDE
## ***ACTION*** KILL 1 of the Kafka Brokers
## ***ACTION*** OBSERVE Producer terminal messages or lack thereof

### Why did the send not fail for acks 1?

Setting the Producer config ProducerConfig.ACKS_CONFIG (acks config for producer) to “1”.
This setting expects leader to only give successful ack after it writes to its log.
Replicas still get replication but leader does not wait for replication to send ack.
Broker Config min.insync.replicas is still set to 3, but this config only gets looked at if acks=“all”.

### Running describe topics before and after stoping broker

Try the last steps again. Stop a server while producer is running.
Then run describe-topics.sh. Then Rerun server you stopped and run describe-topics.sh again.
Observe the changes to ISRs and partition to broker ownership.



#### bin/describe-topics.sh
```sh
#!/usr/bin/env bash

cd ~/kafka-training

# List existing topics
kafka/bin/kafka-topics.sh --describe \
    --topic stock-prices \
    --zookeeper localhost:2181

```

The script bin/describe-topics.sh calls kafka-topics.sh to describe the topic layout with regards to brokers 
and partitions.

## ***ACTION*** REPEAT Last experiment, but this time run bin/describe-topics.sh and observe.

### Retry with acks = 0
Run the last example again (servers, and producer), but this time set acks to 0.
Run all three brokers then take one away. Then take another broker away.
Try Run describe-topics.
Take all of the brokers down and continue to run the producer.
What do you think happens?
When you are done, change acks back to acks=all.



## ***ACTION*** EDIT StockPriceKafkaProducer and set Producer config acks to "0".
## ***ACTION*** RUN all brokers and zookeeper if needed.
## ***ACTION*** RUN StockPriceKafkaProducer from the IDE
## ***ACTION*** REPEAT Last experiment, but this time run bin/describe-topics.sh and observe.

### Lab Review

Look at the following listing.

#### Describe topic listing
```
$ bin/describe-topics.sh
Topic:stock-prices	PartitionCount:3	ReplicationFactor:3	Configs:min.insync.replicas=2
	Topic: stock-prices	Partition: 0	Leader: 2	Replicas: 1,2,0	Isr: 2
	Topic: stock-prices	Partition: 1	Leader: 2	Replicas: 2,0,1	Isr: 2
	Topic: stock-prices	Partition: 2	Leader: 2	Replicas: 0,1,2	Isr: 2
```

* How would you describe the above?
* How many servers are likely running out of the three?
* Would the producer still run with acks=all? Why or Why not?
* Would the producer still run with acks=1? Why or Why not?
* Would the producer still run with acks=0? Why or Why not?
* Which broker is the leader of partition 1?


* How would you describe the above? Two servers are down. Broker 0 and Broker 1.
* How many servers are likely running out of the three? Just the third broker.
* Would the producer still run with acks=all? Why or Why not? No. Only one server is running.
* Would the producer still run with acks=1? Why or Why not? No. Yes. The 3rd server owns the partitions.
* Would the producer still run with acks=0? Why or Why not? Yes.
* Which broker is the leader of partition 1? The third server one with broker id 2.

____

# Kafka Tutorial

This comprehensive *Kafka tutorial* covers Kafka architecture and design. The *Kafka tutorial* has example 
Java Kafka producers and Kafka consumers. The *Kafka tutorial* also covers Avro and Schema Registry.

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
