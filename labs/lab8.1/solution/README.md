# Lab 8.1: Kafka SSL

Welcome to the session 8 lab 1. The work for this lab is done in `~/kafka-training/lab8.1`.
In this lab, you are going to setup SSL.

Please refer to the [Kafka course notes](https://goo.gl/a4kk5b) for any updates or changes to this lab.

Find the latest version of this lab [here]().

## Kafka and SSL

Use the slides as a guide for this lab.

## ***ACTION*** - EDIT `bin/create-ssl-key-keystore.sh` and follow instructions

## Running create-ssl-key-keystore.sh

You will want to run `create-ssl-key-keystore.sh` and then
copy and/or move files so that each Broker, Producer or Consumer has access
to `/opt/kafka/conf/certs/`.

#### Running create-ssl-key-keystore.sh
```sh
~/kafka-training/lab8.1/solution

$ bin/create-ssl-key-keystore.sh
Create the cluster key for cluster communication.
Create the Certificate Authority (CA) file to sign keys.
Generating a 1024 bit RSA private key
writing new private key to 'ca-key'
...
Certificate was added to keystore
Import the Signed Cluster Certificate into the key store.
Certificate reply was installed in keystore
```

## ***ACTION*** - RUN `bin/create-ssl-key-keystore.sh`

#### Copying cert files to /opt/kafka/

```sh
$ sudo cp -R resources/opt/kafka/ /opt/
```

## ***ACTION*** - COPY output of `bin/create-ssl-key-keystore.sh` to `/opt/kafka/`

## Configuring Kafka Servers
You will need to configure the listeners protocols for each server. In this example, we
are using three servers. You will want to configure Kafka so it is available on SSL and plaintext.
The plaintext important for tools, and you could block Plaintext at firewalls or using routes.

You will need to pass in the truststore and keystore locations and passwords.

The setting `security.inter.broker.protocol=SSL` may not be needed if Kafka a cluster runs in
a single private subnet. Remember that SSL makes it Kafka run slower, and adds extra CPU load
on Kafka Brokers.

## ***ACTION*** - EDIT `config/server-0.properties` and follow instructions
## ***ACTION*** - EDIT `config/server-1.properties` and follow instructions
## ***ACTION*** - EDIT `config/server-2.properties` and follow instructions

## Configure Kafka Consumer

You will need to pass in truststore and keystore locations and passwords to the consumer.

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/consumer/ConsumerUtil.java` and follow instructions in file.


## Configure Kafka Producer

You will need to pass in truststore and keystore locations and passwords to the producer.

## ***ACTION*** - EDIT `src/main/java/com/cloudurable/kafka/producer/ProducerUtils.java` and follow instructions in file.
## ***ACTION*** - RUN ZooKeeper and three Kafka Brokers (scripts are under bin for ZooKeeper and Kafka Brokers).
## ***ACTION*** - RUN ConsumerBlueMain from the IDE
## ***ACTION*** - RUN StockPriceProducer from the IDE

## Expected results
You should be able to send records from the producer to the broker
and read records from the consumer to the broker using SSL.
