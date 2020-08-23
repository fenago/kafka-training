# Lab 5.2: Adding a clean shutdown to our producer

Welcome to the session 5 lab 2. The work for this lab is done in `~/kafka-training/lab5.2`.
In this lab, you are going to create a clean shutdown for our advanced Producer.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here](https://gist.github.com/RichardHightower/5708326f9052307d312f90a6fa189eda).



## Lab Adding an orderly shutdown flush and close

### Shutdown Producer Nicely

Let's write some code to shut the Producer down nicely.

The shutdown code will happen if you are running the producer example from a terminal and type ctrl-C so shutdown from Java occurs.
We will write some code to shutdown thread pool and wait. Then we will flush the producer to send any outstanding batches if using batches (producer.flush()). Lastly, we will close the producer using producer.close and wait five seconds for the producer to shutdown.
(Note that closing the producer also flushes it.)




To this we will add shutdown hook to Java runtime. Then to test we will start the StockPriceKafkaProducer, and then you can stop it using
CTRL-C or by pressing the stop button in your IDE.



#### ~/kafka-training/lab5.2/src/main/java/com/cloudurable/kafka/producer/StockPriceKafkaProducer.java
#### Kafka Producer:  StockPriceKafkaProducer shutdown hook for clean shutdown
```java
public class StockPriceKafkaProducer {

    ...

    private static final Logger logger = LoggerFactory.getLogger(StockPriceKafkaProducer.class);



    public static void main(String... args) throws Exception {
        //Create Kafka Producer
        final Producer<String, StockPrice> producer = createProducer();
        //Create StockSender list
        final List<StockSender> stockSenders = getStockSenderList(producer);

        //Create a thread pool so every stock sender gets it own.
        // Increase by 1 to fit metrics.
        final ExecutorService executorService =
                Executors.newFixedThreadPool(stockSenders.size() );

        //Run each stock sender in its own thread.
        stockSenders.forEach(executorService::submit);


        //Register nice shutdown of thread pool, then flush and close producer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
                logger.info("Flushing and closing producer");
                producer.flush();
                producer.close(10_000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("shutting down", e);
            }
        }));
    }

...
}

```

Notice we add a shutdown hook using Runtime.getRuntime().addShutdownHook and this shutdown hook that
shuts down the thread pool, then calls flush on the producer and then closes the producer whilst waiting 10 seconds for the close to happen.


## ***ACTION*** - EDIT src/main/java/com/cloudurable/kafka/producer/StockPriceKafkaProducer.java and add a shutdown hook to main.
## ***ACTION*** - RUN this StockPriceKafkaProducer and try shutting it down.

____

# Kafka Tutorial

This comprehensive *Kafka tutorial* covers Kafka architecture and design. The *Kafka tutorial* has example Java Kafka producers and Kafka consumers. The *Kafka tutorial* also covers Avro and Schema Registry.

[Complete Kafka Tutorial: Architecture, Design, DevOps and Java Examples.](http://cloudurable.com/blog/kafka-tutorial-kafka-producer-advanced-java-examples/index.html "Comprehensive Apache Kafka tutorial and training series")


* [Kafka Tutorial Part 1: What is Kafka?](http://cloudurable.com/blog/what-is-kafka/index.html "This Kafka tutorial describes what Kafka is. Kafka is a fast, scalable, durable, and fault-tolerant publish-subscribe messaging system, Kafka is used in use cases where JMS, RabbitMQ, and AMQP may not even be considered due to volume and responsiveness. It covers the impact of Kafka, who uses it and why it is important")
* [Kafka Tutorial Part 2: Kafka Architecture](http://cloudurable.com/blog/kafka-architecture/index.html "This Kafka tutorial discusses the structure of Kafka. Kafka consists of Records, Topics, Consumers, Producers, Brokers, Logs, Partitions, and Clusters. Records can have key, value and timestamp. Kafka Records are immutable. A Kafka Topic is a stream of records - "/orders", "/user-signups". You can think of a Topic as a feed name. It covers the structure of and purpose of topics, log, partition, segments, brokers, producers, and consumers")
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
