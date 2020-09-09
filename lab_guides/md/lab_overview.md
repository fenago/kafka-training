<img align="right" src="./logo.png">

<h2><span style="color:red;">Apache Kafka</span></h2>

Apache Kafka is an open-source distributed event streaming platform used by thousands of companies for high-performance data pipelines, streaming analytics, data integration, and mission-critical applications.

### Labs

- **Kafka Tutorial Part 1:** What is Kafka?
    This Kafka tutorial describes what Kafka is. Kafka is a fast, scalable, durable, and fault-tolerant publish-subscribe messaging system, Kafka is used in use cases where JMS, RabbitMQ, and AMQP may not even be considered due to volume and responsiveness. It covers the impact of Kafka, who uses it and why it is important
- **Kafka Tutorial Part 2:** Kafka Architecture
    This Kafka tutorial discusses the structure of Kafka. Kafka consists of Records, Topics, Consumers, Producers, Brokers, Logs, Partitions, and Clusters. Records can have key, value and timestamp. Kafka Records are immutable. A Kafka Topic is a stream of records - **\    /orders\    , \    /user-signups\    . You can think of a Topic as a feed name. It covers the structure of and purpose of topics, log, partition, segments, brokers, producers, and consumers
- **Kafka Tutorial Part 3:** Kafka Topic Architecture
    This Kafka tutorial covers some lower level details of Kafka topic architecture. It is a continuation of the Kafka Architecture article. This article covers Kafka Topic's Architecture with a discussion of how partitions are used for fail-over and parallel processing.
- **Kafka Tutorial Part 4:** Kafka Consumer Architecture
    This Kafka tutorial covers Kafka Consumer Architecture with a discussion consumer groups and how record processing is shared among a consumer group as well as failover for Kafka consumers.
- **Kafka Tutorial Part 5:** Kafka Producer Architecture
    This Kafka tutorial covers Kafka Producer Architecture with a discussion of how a partition is chosen, producer cadence, and partitioning strategies.
- **Kafka Tutorial Part 6:** Using Kafka from the command line
    This Kafka tutorial covers using Kafka from the command line starts up ZooKeeper, and Kafka and then uses Kafka command line tools to create a topic, produce some messages and consume them.
- **Kafka Tutorial Part 7:** Kafka Broker Failover and Consumer Failover
    This Kafka tutorial covers creating a replicated topic. Then demonstrates Kafka consumer failover and Kafka broker failover. Also demonstrates load balancing Kafka consumers. Article shows how, with many groups, Kafka acts like a Publish/Subscribe message broker. But, when we put all of our consumers in the same group, Kafka will load share the messages to the consumers in the same group like a queue.
- **Kafka Tutorial Part 8:** Kafka Ecosystem
    This Kafka tutorial covers Kafka ecosystem:** Kafka Core, Kafka Streams, Kafka Connect, Kafka REST Proxy, and the Schema Registry
- **Kafka Tutorial Part 9:** Kafka Low-Level Design
    This Kafka tutorial is a discussion of Kafka Architecture regarding low-level design details for scale failover, and recovery.
- **Kafka Tutorial Part 10:** Kafka Log Compaction Architecture
    This Kafka tutorial covers Kafka log compaction. Kafka can delete older records based on time or size of a log. Kafka also supports log compaction for record key compaction. Log compaction means that Kafka will keep the latest version of a record and delete the older versions during a log compaction.
- **Kafka Tutorial Part 11:** Writing a Kafka Producer example in Java
    This Kafka tutorial covers creating a Kafka Producer in Java and shows a Java Kafka Producer Example
- **Kafka Tutorial Part 12:** Writing a Kafka Consumer example in Java
    This Kafka tutorial covers creating a Kafka Consumer in Java and shows a Java Kafka Consumer Example
- **Kafka Tutorial Part 13:** Writing Advanced Kafka Producer Java examples
    This tutorial covers advanced producer topics like custom serializers, ProducerInterceptors, custom Partitioners, timeout, record batching & linger, and compression.
* Kafka Tutorial 14:** Writing Advanced Kafka Consumer Java examples
- **Kafka Tutorial Part 15:** Kafka and Avro
    This Kafka tutorial covers Avro data format, defining schemas, using schemas for Big Data and Data Streaming Architectures with an emphasis on Kafka
- **Kafka Tutorial Part 16:** Kafka and Schema Registry
    This Kafka tutorial covers Kafka Avro serialization and operations of the Schema Registry. Also covers using Avro Schema Evolution with the Schema Registry


