# Lab 8.3: Kafka Security SASL SCRAM

Welcome to the session 8 lab 4. The work for this lab is done in `~/kafka-training/lab8.4`.
In this lab, you are going to Kafka SASL SCRAM.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here]().

## Kafka and SASL PLAIN

SCRAM is Salted Challenge Response Authentication Mechanism (RFC 5802) . SCRAM is a SASL
mechanism that addresses security concerns with traditional mechanisms and is better
than PLAIN and DIGEST-MD5.


Use the slides as a guide for this lab.

## Create SCRAM Users

Create the users admin, stocks_consumer, stocks_producer and store these users in ZooKeeper
using the `kafka-configs.sh` utility.



#### Create SCRAM Users using kafka-configs.sh
#### bin/create-scram-users.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training
SCRAM_CONFIG='SCRAM-SHA-256=[iterations=8192,password=kafka123]'
SCRAM_CONFIG="$SCRAM_CONFIG,SCRAM-SHA-512=[password=kafka123]"

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name stocks_consumer
    --zookeeper localhost:2181 \

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name stocks_producer
    --zookeeper localhost:2181 \

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name admin
    --zookeeper localhost:2181 \


```

Note we are using PlainLoginModule from Kafka.

## ***ACTION*** EDIT bin/create-scram-users.sh and follow instructions in file
## ***ACTION*** RUN bin/create-scram-users.sh

## Modify JAAS config for Kafka Brokers to use Scram

Modify the JAAS config for the Broker to use scram for KafkaServer and
Plain for ZooKeeper Client.

#### JAAS config file for Broker
#### resources/opt/kafka/conf/security/kafka_broker_jaas.conf
```sh
KafkaServer {
  org.apache.kafka.common.security.scram.ScramLoginModule required
  username="admin"
  password="kafka123";

};

// Zookeeper client authentication
Client {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="kafka-123";
};

```

## ***ACTION*** - EDIT `resources/opt/kafka/conf/security/kafka_broker_jaas.conf` and follow instructions in file


## Modify Kafka Brokers Config properties file add SCRAM config

## ***ACTION*** - EDIT config/server-0.properties and follow directions
## ***ACTION*** - EDIT config/server-1.properties and follow directions
## ***ACTION*** - EDIT config/server-2.properties and follow directions


## Modify JAAS config for Consumer add user

## ***ACTION*** - EDIT resources/opt/kafka/conf/security/kafka_consumer_stocks_jaas.conf and follow directions


## Modify Consumer createConsumer() add SASL config and JAAS config location

## ***ACTION*** - EDIT src/main/java/com/cloudurable/kafka/consumer/ConsumerUtil.java and follow directions


## Modify JAAS config for Producer add user

## ***ACTION*** - EDIT resources/opt/kafka/conf/security/kafka_producer_stocks_jaas.conf and follow directions

## Modify Producer createProducer()  add SASL config and JAAS config location

## ***ACTION*** - EDIT src/main/java/com/cloudurable/kafka/producer/support/StockPriceProducerUtils.java and follow directions

## Run the lab

## ***ACTION*** - COPY JAAS files `cp -R resources/opt/kafka/conf/security /opt/kafka/conf/`
## ***ACTION*** - RUN ZooKeeper and three Kafka Brokers (scripts are under bin for ZooKeeper and Kafka Brokers).
## ***ACTION*** - RUN ConsumerBlueMain from the IDE
## ***ACTION*** - RUN StockPriceProducer from the IDE

## Expected results
You should be able to send records from the producer to the broker
and read records from the consumer to the broker using SASL SCRAM auth.






