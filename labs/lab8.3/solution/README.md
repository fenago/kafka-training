# Lab 8.3: Kafka Security SASL PLAIN

Welcome to the session 8 lab 3. The work for this lab is done in `~/kafka-training/lab8.3`.
In this lab, you are going to Kafka SASL PLAIN.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here]().

## Kafka and SASL PLAIN

Use the slides as a guide for this lab.

## Create JAAS config for ZooKeeper add admin user

To log into ZooKeeper, you would need user admin and and a password (kafka-123).
You would configure this via JAAS file called zookeeper_jass.conf
which will live under /opt/kafka/config/security/zookeeper_jass.conf.

#### ZooKeeper JAAS file
#### resources/opt/kafka/conf/security/kafka_broker_jaas.conf
```sh
// Zookeeper server authentication
Server {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="kafka-123"
  user_admin="kafka-123";
};
```

Note we are using PlainLoginModule from Kafka.

## ***ACTION*** EDIT resources/opt/kafka/conf/security/kafka_broker_jaas.conf and follow instructions in file

## Modify ZooKeeper properties file add SASL config


We need ZooKeeper to use `org.apache.zookeeper.server.auth.SASLAuthenticationProvider`
as its authProvider. This authProvider requires JaaS login via SASL config/zookeeper.properties.

#### Use Kafka SASLAuthenticationProvider from ZooKeeper
#### config/zookeeper.properties
```sh
dataDir=/tmp/zookeeper-secure2
clientPort=2181
maxClientCnxns=0

authProvider.1=org.apache.zookeeper.server.auth.SASLAuthenticationProvider
requireClientAuthScheme=sasl
jaasLoginRenew=3600000

```

Note we are using SASLAuthenticationProvider from Kafka.

## ***ACTION*** EDIT config/zookeeper.properties and follow instructions in file


## Modify ZooKeeper startup script add JAAS config location

#### Make ZooKeeper use JAAS config file
#### bin/run-zookeeper.sh
```sh
#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

export KAFKA_JAAS_FILE="/opt/kafka/conf/security/zookeeper_jaas.conf"
export KAFKA_OPTS="-Djava.security.auth.login.config=$KAFKA_JAAS_FILE"

## Run ZooKeeper
kafka/bin/zookeeper-server-start.sh \
   "$CONFIG/zookeeper.properties"

```

## ***ACTION*** EDIT bin/run-zookeeper.sh and follow instructions in file

## Create JAAS config for Kafka Brokers add users (admin, consumer, producer)

We will also need a JAAS config file for the broker which will live under
`/opt/kafka/conf/security/kafka_broker_jaas.conf`.
This JAAS config file will sets up users for admin for zookeeper,
and for inter-broker communication, as well as set up users for consumers and producers.

#### JAAS config file for Broker
#### resources/opt/kafka/conf/security/kafka_broker_jaas.conf
```sh
KafkaServer {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="kafka-123"
  user_admin="kafka-123"
  user_stocks_consumer="consumer123"
  user_stocks_producer="producer123";
};

// Zookeeper client authentication
Client {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="kafka-123";
};

```

## ***ACTION*** - EDIT `resources/opt/kafka/conf/security/kafka_broker_jaas.conf` and follow instructions in file


## Modify Kafka Brokers Config properties file add SASL config

## ***ACTION*** - EDIT config/server-0.properties and follow directions
## ***ACTION*** - EDIT config/server-1.properties and follow directions
## ***ACTION*** - EDIT config/server-2.properties and follow directions

## Modify Kafka Broker startup script add JAAS config location

## ***ACTION*** - EDIT bin/start-1st-server.sh and follow directions
## ***ACTION*** - EDIT bin/start-2nd-server.sh and follow directions
## ***ACTION*** - EDIT bin/start-3rd-server.sh and follow directions


## Create JAAS config for Consumer add user

## ***ACTION*** - EDIT resources/opt/kafka/conf/security/kafka_consumer_stocks_jaas.conf and follow directions


## Modify Consumer createConsumer() add SASL config and JAAS config location

## ***ACTION*** - EDIT src/main/java/com/cloudurable/kafka/consumer/ConsumerUtil.java and follow directions


## Create JAAS config for Producer add user

## ***ACTION*** - EDIT resources/opt/kafka/conf/security/kafka_producer_stocks_jaas.conf and follow directions

## Modify Producer createProducer()  add SASL config and JAAS config location

## ***ACTION*** - EDIT src/main/java/com/cloudurable/kafka/producer/support/StockPriceProducerUtils.java and follow directions

## Run the lab

## ***ACTION*** - COPY resource files to /opt/kafka
## ***ACTION*** - RUN ZooKeeper and three Kafka Brokers (scripts are under bin for ZooKeeper and Kafka Brokers).
## ***ACTION*** - RUN ConsumerBlueMain from the IDE
## ***ACTION*** - RUN StockPriceProducer from the IDE

## Expected results
You should be able to send records from the producer to the broker
and read records from the consumer to the broker using SASL PLAIN auth.






