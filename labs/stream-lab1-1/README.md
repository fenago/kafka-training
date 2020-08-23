
# LAB Stream 1-1 Kafka Word Count Example

Welcome to the session Stream 1-1 lab. The work for this lab is done in `~/kafka-training/stream-lab1`.
In this lab, you are going to run the Kafka Word Count Example.
You create two new Kafka topics, one called `streams-plaintext-input` and the other `streams-plaintext-output`
You will then add some data to the input topic, start a consumer on the output topic so you can watch it, 
and then run the Word Count Demo

## ***ACTION*** - START ZooKeeper and Kafka Broker if needed.
Only 1 broker is necessary.

## Create Kafka Topics

* Create a topic named `streams-plaintext-input` with 1 partition and a replication factor of 1.
* Create a topic named `streams-wordcount-output` with 1 partition and a replication factor of 1.

## ***ACTION*** - REVIEW `create-topics.sh`

#### ~/kafka-training/stream-lab1/bin/create-topics.sh

```sh
#!/usr/bin/env bash
cd ~/kafka-training

## Create input topic
kafka/bin/kafka-topics.sh --create \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-plaintext-input \
    --zookeeper localhost:2181

## Create output topic
kafka/bin/kafka-topics.sh --create \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-wordcount-output \
    --zookeeper localhost:2181

## List created topics
kafka/bin/kafka-topics.sh --list \
    --zookeeper localhost:2181
```

## ***ACTION*** - RUN `create-topics.sh` as follows:

```sh
$ cd ~/kafka-training/stream-lab1/bin          
$ ./create-topics.sh                        
Created topic "streams-plaintext-input".   
Created topic "streams-wordcount-output".   
streams-plaintext-input
streams-wordcount-output
```

## Add data to the topic
Now add data to the input topic by starting a console producer and entering data.

## ***ACTION*** - REVIEW `start-producer-console-input.sh`

#### ~/kafka-training/stream-lab1/bin/start-producer-console-input.sh

```sh
#!/usr/bin/env bash
cd ~/kafka-training

## Producer
kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
```

## ***ACTION*** - RUN `start-producer-console-input.sh`

```sh
$ cd ~/kafka-training/stream-lab1
$ ./start-producer-console-input.sh
>
```

## ****ACTION**** - Enter Data

Enter the following 3 lines of data. Ctrl-C to stop the console.
```
> stock streams MSFT
> stock data AAPL
> stock streams RHT
```

## ****ACTION**** - Validate Data

Run a console consumer against the input topic to verify the data is there.

#### ~/kafka-training/stream-lab1/start-consumer-console-input.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

## Input Consumer
kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input --from-beginning
```

## ***ACTION*** - RUN `start-consumer-console-input.sh`
```
$ cd ~/kafka-training/stream-lab1/bin
$ ./start-consumer-console-input.sh
stock streams MSFT
stock data AAPL
stock streams RHT
<Ctrl-C>
Processed a total of 3 messages
```

Above, <Ctrl-C> stops the consumer.

## Run a console consumer against the output.

Run another consumer against the output topic so we can see the work that the Word Count Demo does.
Notice a few things about this consumer. These match the way the topic is populated by the demo.
* It has a message formatter specified.
* It has key and value deserializers.

## ***ACTION*** - EDIT `~/kafka-training/stream-lab1/bin/start-consumer-console-output.sh`, follow instructions in file.

#### ~/kafka-training/stream-lab1/start-consumer-console-output.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

## Output Consumer
kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

## ***ACTION*** - RUN `start-consumer-console-output.sh`
```
$ cd ~/kafka-training/stream-lab1/bin
$ ./start-consumer-console-output.sh
```

There will be no output until the demo code runs. Keep this window open so you can see it as the demo runs.

## Run the Word Count demo
Kafka provides examples, one of which is the Word Count demo. 
It consumes, via the KStream dsl (java api) data from the input topic and writes to the output topic.

> See the documentation and the java code for the [Word Count demo in github](https://github.com/apache/kafka/blob/trunk/streams/examples/src/main/java/org/apache/kafka/streams/examples/wordcount/WordCountDemo.java)

Since the demo is included in the Kafka libraries, we can use Kafka's `kafka-run-class.sh` script to run it.

## ***ACTION*** - REVIEW `run-word-count-demo.sh`

#### ~/kafka-training/stream-lab1/run-word-count-demo.sh

```sh
#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-run-class.sh org.apache.kafka.streams.examples.wordcount.WordCountDemo
```

## ***ACTION*** - RUN `run-word-count-demo.sh`

```sh
$ cd ~/kafka-training/stream-lab1/bin
$ ./run-word-count-demo.sh
```

#### Consumer Console Output after running run-word-count-demo.sh
```
stock   1
streams 1
msft    1
stock   2
data    1
aapl    1
stock   3
streams 2
rht     1
```

### Conclusion Word Count example

The word count is the simplest example of stream processing of an input topic and writing results to a different
then you created Kafka Producer in Java that uses the Kafka replicated topic to send records.
You sent records with the Kafka Producer using async and sync send methods.

### Review Word Count example


<br />
<br />


#### Related content

* [What is Kafka?](http://cloudurable.com/blog/what-is-kafka/index.html "This article describes what Kafka is. Kafka is a fast, scalable, durable, and fault-tolerant publish-subscribe messaging system, Kafka is used in use cases where JMS, RabbitMQ, and AMQP may not even be considered due to volume and responsiveness. It covers the impact of Kafka, who uses it and why it is important")
* [Kafka Architecture](http://cloudurable.com/blog/kafka-architecture/index.html "This article discusses the structure of Kafka. Kafka consists of Records, Topics, Consumers, Producers, Brokers, Logs, Partitions, and Clusters. Records can have key, value and timestamp. Kafka Records are immutable. A Kafka Topic is a stream of records - \"/orders\", \"/user-signups\". You can think of a Topic as a feed name. It covers the structure of and purpose of topics, log, partition, segments, brokers, producers, and consumers")
* [Kafka Topic Architecture](http://cloudurable.com/blog/kafka-architecture-topics/index.html "This article covers some lower level details of Kafka topic architecture. It is a continuation of the Kafka Architecture article. This article covers Kafka Topic's Architecture with a discussion of how partitions are used for fail-over and parallel processing.")
* [Kafka Consumer Architecture](http://cloudurable.com/blog/kafka-architecture-consumers/index.html "Covers Kafka Consumer Architecture with a discussion consumer groups and how record processing is shared among a consumer group as well as failover for Kafka consumers.")
* [Kafka Producer Architecture](http://cloudurable.com/blog/kafka-architecture-producers/index.html "Covers Kafka Producer Architecture with a discussion of how a partition is chosen, producer cadence, and partitioning strategies.")
* [Kafka Architecture and low level design](http://cloudurable.com/blog/kafka-architecture-low-level/index.html "Discussion of Kafka Architecture regarding low-level design details for scale failover, and recovery.")
* [Kafka and Schema Registry](http://cloudurable.com/blog/kafka-avro-schema-registry/index.html "Covers Kafka Avro serialization and operations of the Schema Registry. Also covers using Avro Schema Evolution with the Schema Registry")
* [Kafka and Avro](http://cloudurable.com/blog/avro/index.html "Covers Avro data format, defining schemas, using schemas for Big Data and Data Streaming Architectures with an emphasis on Kafka")
* [Kafka Ecosystem](http://cloudurable.com/blog/kafka-ecosystem/index.html "Kafka Ecosystem: Kafka Core, Kafka Streams, Kafka Connect, Kafka REST Proxy, and the Schema Registry")
* [Kafka vs. JMS](http://cloudurable.com/blog/kafka-vs-jms/index.html "Kafka Architecture. Covers Kafka vs. JMS, RabbitMQ and other MOMs.")
* [Kafka versus Kinesis](http://cloudurable.com/blog/kinesis-vs-kafka/index.html "Kafka Architecture. Compares Kafka to Kinesis.")
* [Kafka Tutorial: Using Kafka from the command line](http://cloudurable.com/blog/kafka-tutorial-kafka-from-command-line/index.html "Kafka Training: Using Kafka from the command line starts up ZooKeeper, and Kafka and then uses Kafka command line tools to create a topic, produce some messages and consume them.")
* [Kafka Tutorial: Kafka Broker Failover and Consumer Failover](http://cloudurable.com/blog/kafka-tutorial-kafka-failover-kafka-cluster/index.html "Kafka Tutorial: Covers creating a replicated topic. Then demonstrates Kafka consumer failover and Kafka broker failover. Also demonstrates load balancing Kafka consumers. Article shows how, with many groups, Kafka acts like a Publish/Subscribe message broker. But, when we put all of our consumers in the same group, Kafka will load share the messages to the consumers in the same group like a queue.")
* [Kafka Tutorial](http://cloudurable.com/ppt/kafka-tutorial-cloudruable-v2.pdf "PDF slides for a Kafka Tutorial")
* [Kafka Tutorial: Writing a Kafka Producer example in Java](http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html "Kafka Tutorial: Covers creating a Kafka Producer in Java and shows a Java Kafka Producer Example")
* [Kafka Tutorial: Writing a Kafka Consumer example in Java](http://cloudurable.com/blog/kafka-tutorial-kafka-consumer/index.html "Kafka Tutorial: Covers creating a Kafka Consumer in Java and shows a Java Kafka Consumer Example")
* [Kafka Architecture: Log Compaction](http://cloudurable.com/blog/kafka-architecture-log-compaction/index.html)

<br />

#### About Cloudurable
We hope you enjoyed this article. Please provide [feedback](http://cloudurable.com/contact/index.html).
Cloudurable provides [Kafka training](http://cloudurable.com/kafka-training/index.html "Onsite, Instructor-Led, Kafka Training"), [Kafka consulting](http://cloudurable.com/kafka-aws-consulting/index.html), [Kafka support](http://cloudurable.com/subscription_support/index.html) and helps [setting up Kafka clusters in AWS](http://cloudurable.com/services/index.html).
