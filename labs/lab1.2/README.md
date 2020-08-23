
# Lab 1.2 Getting started with Kafka cluster
## Understanding Kafka Failover

Welcome to session 1 lab 2.
The work for this lab is done in `~/kafka-training/lab1.2`.
This Kafka lab picks up right where the first lab left off.
The first lab has instructions on how to run ZooKeeper and use Kafka utils.
Please refer to Kafka [course notes](https://gist.github.com/RichardHightower/c6fe36856a581728b4187e4d38232903)
for any changes. The latest version of this lab instructions can be found
[here](https://gist.github.com/RichardHightower/42c78c0764990a76b8ed4b20e479fe98).

In this lab, we are going to run many Kafka Nodes on our development machine so that you
will need at least 16 GB of RAM for local dev machine. You can run just two servers if
you have less memory than 16 GB. We are going to create a replicated topic.

We then demonstrate consumer failover and broker failover. We also demonstrate load
balancing Kafka consumers. We show how, with many groups, Kafka acts like a Publish/Subscribe.
But, when we put all of our consumers in the same group, Kafka will load share the messages
to the consumers in the same group (more like a queue than a topic in a traditional MOM sense).


If not already running, then start up ZooKeeper (`./run-zookeeper.sh` from the first lab).
Also, shut down Kafka from the first tutorial (use kill or CTRL-C from the terminal which is running Kafka).

Next, you need to copy server properties for three brokers
(detailed instructions to follow). Then we will modify these Kafka server properties to add unique
Kafka ports, Kafka log locations, and unique Broker ids. Then we will create three scripts to start these
servers up using these properties, and then start the servers. Lastly, we create replicated topic and use it
to demonstrate Kafka consumer failover, and Kafka broker failover.


<br />

### Create three new Kafka server-n.properties files

In this section, we will copy the existing Kafka `server.properties` to `server-0.properties`, `server-1.properties`,
and `server-2.properties`.
Then we change `server-0.properties` to set `log.dirs` to `“./logs/kafka-0`.
Then we modify `server-1.properties` to set `port` to `9093`, broker `id` to `1`, and `log.dirs`
to `“./logs/kafka-1”`.
Lastly modify  `server-2.properties` to use `port` 9094, broker `id` 2, and `log.dirs` `“./logs/kafka-2”`.

You modify the port by modifying the listeners `listeners=PLAINTEXT://localhost:9092`.

## ***ACTION*** COPY server.properites file three times to server-0.properties, server-1.properties and
server-2.properties as follows:

#### Copy server properties file
```sh
$ cd ~/kafka-training
$ mkdir -p lab1.2/config
$ cp kafka/config/server.properties lab1.2/config/server-0.properties
$ cp kafka/config/server.properties lab1.2/config/server-1.properties
$ cp kafka/config/server.properties lab1.2/config/server-2.properties
```


<br />


## ***ACTION*** EDIT `~/kafka-training/lab1.2/config/server-0.properties` as follows:

With your favorite text editor change server-0.properties so that `log.dirs` is set to `./logs/kafka-0`.
Leave the rest of the file the same. Make sure `log.dirs` is only defined once.



<br />

#### ~/kafka-training/lab1.2/config/server-0.properties

```sh
broker.id=0
listeners=PLAINTEXT://localhost:9092
log.dirs=./logs/kafka-0
...
```

<br />

## ***ACTION*** EDIT `~/kafka-training/lab1.2/config/server-1.properties` as follows:

With your favorite text editor change `log.dirs`, `broker.id` and and `log.dirs` of `server-1.properties` as follows:

#### ~/kafka-training/lab1.2/config/server-1.properties

```sh
broker.id=1
listeners=PLAINTEXT://localhost:9093
log.dirs=./logs/kafka-1
...
```


<br />

## ***ACTION*** EDIT `~/kafka-training/lab1.2/config/server-2.properties` as follows:

With your favorite text editor change `log.dirs`, `broker.id` and and `log.dirs` of `server-2.properties` as follows:

#### ~/kafka-training/lab1.2/config/server-2.properties

```sh
broker.id=2
listeners=PLAINTEXT://localhost:9094
log.dirs=./logs/kafka-2
...
```


<br />


### Finish creating Startup scripts for three Kafka servers

The startup scripts will just run `kafka-server-start.sh` with the corresponding properties file.

## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-1st-server.sh` and follow instructions in file.

<br />

#### ~/kafka-training/lab1.2/start-1st-server.sh

```sh
#!/usr/bin/env bash
CONFIG=`pwd`/config

cd ~/kafka-training

## Run Kafka
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-0.properties"

```


<br />


## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-2nd-server.sh` and follow instructions in file.


#### ~/kafka-training/lab1.2/start-2nd-server.sh

```sh
#!/usr/bin/env bash
CONFIG=`pwd`/config

cd ~/kafka-training

## Run Kafka
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-1.properties"

```


<br />



## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-3rd-server.sh` and follow instructions in file.


#### ~/kafka-training/lab1.2/start-3rd-server.sh

```sh
#!/usr/bin/env bash
CONFIG=`pwd`/config

cd ~/kafka-training

## Run Kafka
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-2.properties"

```


<br />

Notice we are passing the Kafka server properties files that we created in the last step.

## ***ACTION*** RUN all three Kafka servers as follows:

Now run all three in separate terminals/shells.

#### Run Kafka servers each in own terminal from ~/kafka-training/lab1.2
```sh
$ cd ~/kafka-training/lab1.2

$ ./start-1st-server.sh

...
$ ./start-2nd-server.sh

...
$ ./start-3rd-server.sh

```


<br />

Give the servers a minute to startup and connect to ZooKeeper.



<br />



### Create Kafka replicated topic my-failsafe-topic


Now we will create a replicated topic that the console producers and console consumers can use.


## ***ACTION*** EDIT `~/kafka-training/lab1.2/create-replicated-topic.sh` and follow instructions in file.

<br />

#### ~/kafka-training/lab1.2/create-replicated-topic.sh
```sh
#!/usr/bin/env bash

cd ~/kafka-training

kafka/bin/kafka-topics.sh --create \
    --zookeeper localhost:2181 \
    --replication-factor 3 \
    --partitions 13 \
    --topic my-failsafe-topic

```


<br />


Notice that the replication factor gets set to 3, and the topic name is `my-failsafe-topic`, and
like before it has 13 partitions.

## ***ACTION*** RUN `~/kafka-training/lab1.2/create-replicated-topic.sh` as follows:

Then we just have to run the script to create the topic.



<br />

#### Run create-replicated-topic.sh
```sh
~/kafka-training/lab1.2
$ ./create-replicated-topic.sh
```


<br />

### Start Kafka Consumer that uses Replicated Topic

Next, you finish creating a script that starts the consumer and then start the consumer with the script.


<br />

#### ~/kafka-training/lab1.2/start-consumer-console-replicated.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9094,localhost:9092 \
    --topic my-failsafe-topic \
    --from-beginning

```

## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-consumer-console-replicated.sh` and follow instructions in file.


<br />

Notice that a list of Kafka servers is passed to `--bootstrap-server` parameter. Only, two of the three servers
get passed that we ran earlier. Even though only one broker is needed,  the consumer client will learn about
the other broker from just one server. Usually, you list multiple brokers in case there is an outage so that
the client can connect.

Now we just run this script to start the consumer.


<br />

#### Run start-consumer-console-replicated.sh
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
```


<br />

### Start Kafka Producer that uses Replicated Topic

Next, we create a script that starts the producer. Then launch the producer with the script you create.


<br />

#### ~/kafka-training/lab1.2/start-consumer-producer-replicated.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-console-producer.sh \
--broker-list localhost:9092,localhost:9093 \
--topic my-failsafe-topic

```

## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-consumer-producer-replicated.sh` and follow instructions in file.


<br />

## ***ACTION*** START `~/kafka-training/lab1.2/start-consumer-producer-replicated.sh` as follows:

Notice we start Kafka producer and pass it a list of Kafka Brokers to use via the parameter `--broker-list`.

Now use the start producer script to launch the producer as follows.


<br />

#### Run start-producer-console-replicated.sh
```sh
$ cd ~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
```


<br />

## ***ACTION*** SEND messages with producer as follows:

### Now send messages

Now send some message from the producer to Kafka and see those messages consumed by the consumer.



<br />

#### Producer Console
```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
```


<br />

## ***ACTION*** VIEW messages from consumer as follows:

#### Consumer Console
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
```


<br />

## ***ACTION*** START two more consumers and send more messages as follows:
### Now Start two more consumers and send more messages

Now Start two more consumers in their own terminal window and send more messages from the producer.


#### Producer Console
```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
message 1
message 2
message 3
```


<br />

#### Consumer Console 1st
```sh
$ cd ~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
message 1
message 2
message 3
```


<br />

#### Consumer Console 2nd in new Terminal
```sh
$ cd ~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
message 1
message 2
message 3
```


<br />

#### Consumer Console 2nd in new Terminal
```sh
$ cd ~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
Hi Mom
How are you?
How are things going?
Good!
message 1
message 2
message 3
```


<br />

Notice that the messages are sent to all of the consumers because each consumer is in a different consumer group.


### Change consumer to be in their own consumer group

Stop the producers and the consumers from before, but leave Kafka and ZooKeeper running.

Now let's modify the `start-consumer-console-replicated.sh` script to add a Kafka *consumer group*.
We want to put all of the consumers in same *consumer group*.
This way the consumers will share the messages as each consumer in the *consumer group* will get its share
of partitions.


## ***ACTION*** EDIT `~/kafka-training/lab1.2/start-consumer-console-replicated.sh` and add `--consumer-property group.id=mygroup` as follows:

<br />

#### ~/kafka-training/lab1.2/start-consumer-console-replicated.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9094,localhost:9092 \
    --topic my-failsafe-topic \
    --consumer-property group.id=mygroup
```


<br />

Notice that the script is the same as before except we added `--consumer-property group.id=mygroup` which
will put every consumer that runs with this script into the `mygroup` consumer group.

Now we just run the producer and three consumers.

## ***ACTION*** SHUTDOWN old consumers
## ***ACTION*** RUN three consumers in new terminals as follows:

<br />

#### Run this three times - start-consumer-console-replicated.sh
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
```


<br />

## ***ACTION*** RUN producer and send more messages as follows:

#### Run Producer Console
```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh

```


<br />

Now send seven messages from the Kafka producer console.


#### Producer Console
```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
m1
m2
m3
m4
m5
m6
m7
```


<br />

## ***ACTION*** Observer consumer behavior as follows:

Notice that the messages are spread evenly among the consumers.


<br />

#### 1st Kafka Consumer gets m3, m5
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m3
m5

```


<br />

Notice the first consumer gets messages m3 and m5.

<br />


#### 2nd Kafka Consumer gets m2, m6
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m2
m6
```

<br />

Notice the second consumer gets messages m2 and m6.

<br />



#### 3rd Kafka Consumer gets m1, m4, m7
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m1
m4
m7
```


<br />

Notice the third consumer gets messages m1, m4 and m7.


Notice that each consumer in the group got a share of the messages.


<br />

### Kafka Consumer Failover

Next, let's demonstrate consumer failover by killing one of the consumers and
sending seven more messages. Kafka should divide up the work to the consumers that are still running.

## ***ACTION** First, kill the third consumer (CTRL-C in the consumer terminal does the trick).

## ***ACTION*** Now send seven more messages with the Kafka console-producer.


<br />

#### Producer Console - send seven more messages m8 through m14
```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
m1
...
m8
m9
m10
m11
m12
m13
m14
```

<br />

## ***ACTION*** Observer consumer behavior after failover as follows:

Notice that the messages are spread evenly among the remaining consumers.




<br />

#### 1st Kafka Consumer gets m8, m9, m11, m14
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m3
m5
m8
m9
m11
m14

```


<br />

The first consumer got m8, m9, m11 and m14.


<br />

#### 2nd Kafka Consumer gets m10, m12, m13
```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m2
m6
m10
m12
m13
```


<br />

The second consumer got m10, m12, and m13.

We killed one consumer, sent seven more messages, and saw Kafka spread the load
to remaining consumers. ***Kafka consumer failover works!***


<br />

### Create Kafka Describe Topic Script


You can use `kafka-topics.sh` to see how the Kafka topic is laid out among the Kafka brokers.
The  `---describe` will show  partitions, ISRs, and broker partition leadership.


<br />

## ***ACTION*** EDIT ~/kafka-training/lab1.2/describe-topics.sh and follow instrucitons in file.

#### ~/kafka-training/lab1.2/describe-topics.sh
```sh
#!/usr/bin/env bash

cd ~/kafka-training

# List existing topics
kafka/bin/kafka-topics.sh --describe \
    --topic my-failsafe-topic \
    --zookeeper localhost:2181


```


<br />

## ***ACTION*** RUN ~/kafka-training/lab1.2/describe-topics.sh as follows:

Let's run `kafka-topics.sh --describe` and see the topology of our
`my-failsafe-topic`.


<br />

### Run describe-topics


We are going to lists which broker owns (leader of) which partition, and list
replicas and ISRs of each partition.  ISRs are replicas that are up to date.
Remember there are 13 topics.


<br />


#### Topology of Kafka Topic Partition Ownership
```sh
~/kafka-training/lab1.2
$ ./describe-topics.sh
Topic: my-failsafe-topic    PartitionCount: 13    ReplicationFactor: 3    Configs:
    Topic: my-failsafe-topic    Partition: 0    Leader: 2    Replicas: 2,0,1    Isr: 2,0,1
    Topic: my-failsafe-topic    Partition: 1    Leader: 0    Replicas: 0,1,2    Isr: 0,1,2
    Topic: my-failsafe-topic    Partition: 2    Leader: 1    Replicas: 1,2,0    Isr: 1,2,0
    Topic: my-failsafe-topic    Partition: 3    Leader: 2    Replicas: 2,1,0    Isr: 2,1,0
    Topic: my-failsafe-topic    Partition: 4    Leader: 0    Replicas: 0,2,1    Isr: 0,2,1
    Topic: my-failsafe-topic    Partition: 5    Leader: 1    Replicas: 1,0,2    Isr: 1,0,2
    Topic: my-failsafe-topic    Partition: 6    Leader: 2    Replicas: 2,0,1    Isr: 2,0,1
    Topic: my-failsafe-topic    Partition: 7    Leader: 0    Replicas: 0,1,2    Isr: 0,1,2
    Topic: my-failsafe-topic    Partition: 8    Leader: 1    Replicas: 1,2,0    Isr: 1,2,0
    Topic: my-failsafe-topic    Partition: 9    Leader: 2    Replicas: 2,1,0    Isr: 2,1,0
    Topic: my-failsafe-topic    Partition: 10    Leader: 0    Replicas: 0,2,1    Isr: 0,2,1
    Topic: my-failsafe-topic    Partition: 11    Leader: 1    Replicas: 1,0,2    Isr: 1,0,2
    Topic: my-failsafe-topic    Partition: 12    Leader: 2    Replicas: 2,0,1    Isr: 2,0,1
```


<br />

Notice how each broker gets a share of the partitions as leaders and followers.
Also, see how Kafka replicates the partitions on each broker.


<br />

## ***ACTION*** Kill first broker as follows:

### Test Broker Failover by killing 1st server

Let's kill the first broker, and then test the failover.

<br />

#### Kill the first broker

```sh
 $ kill `ps aux | grep java | grep server-0.properties | tr -s " " | cut -d " " -f2`
```


<br />

You can stop the first broker by hitting CTRL-C in the broker terminal or by running the above command.

Now that the first Kafka broker has stopped, let's use Kafka `topics describe` to see that new leaders were elected!


<br />

## ***ACTION*** Run describe-topics again to see leadership change as follows:

```sh
$ cd ~/kafka-training/lab1.2/solution
$ ./describe-topics.sh
Topic:my-failsafe-topic    PartitionCount:13    ReplicationFactor:3    Configs:
    Topic: my-failsafe-topic    Partition: 0    Leader: 2    Replicas: 2,0,1    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 1    Leader: 1    Replicas: 0,1,2    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 2    Leader: 1    Replicas: 1,2,0    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 3    Leader: 2    Replicas: 2,1,0    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 4    Leader: 2    Replicas: 0,2,1    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 5    Leader: 1    Replicas: 1,0,2    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 6    Leader: 2    Replicas: 2,0,1    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 7    Leader: 1    Replicas: 0,1,2    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 8    Leader: 1    Replicas: 1,2,0    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 9    Leader: 2    Replicas: 2,1,0    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 10    Leader: 2    Replicas: 0,2,1    Isr: 2,1
    Topic: my-failsafe-topic    Partition: 11    Leader: 1    Replicas: 1,0,2    Isr: 1,2
    Topic: my-failsafe-topic    Partition: 12    Leader: 2    Replicas: 2,0,1    Isr: 2,1
```

<br />
Notice how Kafka spreads the leadership over the 2nd and 3rd Kafka brokers.


<br />

### Show Broker Failover Worked

Let's prove that failover worked by sending two more messages from the producer console.
Then notice if the consumers still get the messages.


<br />

## ***ACTION*** SEND the message m15 and m16 as follows:

#### Producer Console - send m15 and m16

```sh
~/kafka-training/lab1.2
$ ./start-consumer-producer-replicated.sh
m1
...
m15
m16
```

<br />


## ***ACTION*** OBSERVER consumer behavior as follows:

Notice that the messages are spread evenly among the remaining live consumers.


<br />


#### 1st Kafka Consumer gets m16

```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m3
m5
m8
m9
m11
m14
...
m16

```


<br />


The first Kafka broker gets m16.

#### 2nd Kafka Consumer gets m15

```sh
~/kafka-training/lab1.2
$ ./start-consumer-console-replicated.sh
m2
m6
m10
m12
m13
...
m15
```

<br />


The second Kafka broker gets m15.

Kafka broker Failover WORKS!


<br />


### Kafka Cluster Failover Review

#### Why did the three consumers not load share the messages at first?

They did not load share at first because they were each in a different consumer group. Consumer groups each subscribe to a topic and maintain their own offsets per partition in that topic.

#### How did we demonstrate failover for consumers?

We shut a consumer down. Then we sent more messages. We observed Kafka spreading messages to the remaining cluster.

#### How did we show failover for producers?

We didn't. We showed failover for Kafka brokers by shutting one down, then using the producer console to send two more messages. Then we saw that the producer used the remaining Kafka brokers. Those Kafka brokers then delivered the messages to the live consumers.

#### What tool and option did we use to show ownership of partitions and the ISRs?

We used `kafka-topics.sh` using the `--describe` option.


<br />


### More about Kafka

To learn  about Kafka see [Kafka architecture](http://cloudurable.com/blog/kafka-architecture/index.html), [Kafka topic architecture](http://cloudurable.com/blog/kafka-architecture-topics/index.html) and [Kafka producer architecture](http://cloudurable.com/blog/kafka-architecture-producers/index.html).

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
