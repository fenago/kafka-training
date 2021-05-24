# Lab 8.2: Kafka and SASL

Welcome to the session 8 lab 2. The work for this lab is done in `~/kafka-training/lab8.2`.
In this lab, you are going to setup Kafka SSL support.



Find the latest version of this lab [here](https://github.com/fenago/kafka-training/tree/master/lab_guides)***

## Authentication

Kafka Broker supports Authentication in producers and consumers, brokers, tools with methods SSL and SASL.

Kafka supports the following SASL mechanisms:

***SASL/GSSAPI Kerberos (GSSAPI - Generic Security Services Application Program Interface -  offers a data-security layer)*** <br>
***SASL/PLAIN (Simple cleartext password mechanism)*** <br>
***SASL/SCRAM-SHA-256 (SCRAM - Salted Challenge Response Authentication Mechanism - modern challenge-response scheme based mechanism with channel binding support)*** <br>
***SASL/SCRAM-SHA-512 (SCRAM - Salted Challenge Response Authentication Mechanism - modern challenge-response scheme based mechanism with channel binding support)*** <br>

## Kafka SASL Authentication - Brokers

Kafka uses JAAS (Java Authentication and Authorization Service) for SASL configuration. <br>
In Kafka Broker JAAS config you have a section name KafkaServer for JAAS file, provides SASL configuration options and how SASL client connections are configured. <br>
In Client section (-Dzookeeper.sasl.client=Client is default) use to authenticate a SASL connection with zookeeper (service name,  Dzookeeper.sasl.client.username=zookeeper by default) an allows Kafka brokers to set SASL ACL on zookeeper nodes. <br>
Locks nodes down so only brokers can modify ZooKeeper nodes. <br>
The same principal must be used by all brokers in the cluster. <br>

## Kafka SASL Authentication - Clients

Clients (Producers and Consumers) configure JAAS using client configuration property `sasl.jaas.config` or using the static JAAS config file <br>
Configure a login module in KafkaClient for the selected mechanism GSSAPI (Kerberos), PLAIN or SCRAM
`-Djava.security.auth.login.config=/opt/kafka/conf/kafka_consumer_stocks_jaas.conf`

#### ~/kafka-training/lab8.2/solution/jaas/kafka_consumer_stocks_jaas.conf
```sh
KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/opt/kafka/conf/security/kafka_consumer.keytab"
  principal="kafka-consumer-stocks@fenago.com";
};
```

## SASL Broker config

Kafka Broker Config : SASL configured with transport PLAINTEXT or SSL <br>
`listeners=SASL_PLAINTEXT://hostname:port`
`listener= SASL_SSL://hostname:port`
`security.inter.broker.protocol=SASL_PLAINTEXT or SASL_SSL`

If SASL_SSL is used, then SSL has to be configured

Kafka SASL Mechanisms:
GSSAPI (Kerberos), PLAIN, SCRAM-SHA-256, SCRAM-SHA-512

## Kafka Authentication using SASL/Kerberos

If you use ***Active Directory*** then no need to set up ***Kerberos server*** <br>
If not using Active Directory you will need to install it <br>
If Oracle Java, download JCE policy files for your Java version to `$JAVA_HOME/jre/lib/security`

## SASL Kerberos: Create Kerberos Principals for Kafka Broker

Ask your Kerberos or Active Directory admin for a principal for each Kafka broker in a cluster.
Ensure all hosts are reachable using hostnames, Kerberos requirement that all hosts are resolvable with FQDNs.

If running your own Kerberos server, create these principals.

```sh
$ sudo /usr/sbin/kadmin.local -q 'addprinc -randkey \
    kafka/{hostname}@{REALM}'
$ sudo /usr/sbin/kadmin.local -q "ktadd -k /etc/security/keytabs/{keytabname}.keytab \
    kafka/{hostname}@{REALM}"
```

## SASL Kerberos: Configuring Kafka Brokers for Kerberos

#### ~/kafka-training/lab8.2/solution/jaas/kafka_broker_jaas.conf
```sh
KafkaServer {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/opt/kafka/conf/security/kafka_broker.keytab"
  principal="kafka/kafka-broker.hostname.com1@fenago.com";
};

// Zookeeper client authentication
Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/opt/kafka/conf/security/kafka_broker.keytab"
  principal="kafka/kafka-broker1.hostname.com@fenago.com";
};
```

Pass to JVM starting up broker. <br>
`-Djava.security.krb5.conf=/etc/kafka/krb5.conf` <br>
`-Djava.security.auth.login.config=/var/kafka/conf/secutiry/ kafka_server_jaas.conf`

## SASL Kerberos: Configuring Kafka Broker Config for Kerberos

Configure SASL port and SASL mechanisms in server.properties as described. <br>
Configure service name (`sasl.kerberos.service.name`). <br>
Match principal name of the kafka brokers from JAAS config on last slide. Recall principal was `kafka/kafka-broker1.hostname.com@fenago.com` . <br>
Set `sasl.enabled.mechanisms` to ***GSSAPI*** (Kerberos). <br>
Set inter broker communication to ***SASL_PLAINTEXT*** or ***SASL_SSL*** .

#### ~/kafka-training/lab8.2/solution/config/server-0.properties
```sh
broker.id=0

listeners=SASL_PLAINTEXT://localhost:9092,SASL_SSL://localhost:10092
sasl.mechanism.inter.broker.protocol=GSSAPI
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka
security.inter.broker.protocol=SASL_PLAINTEXT

ssl.keystore.location=/opt/kafka/conf/certs/kafka.keystore
ssl.keystore.password=kafka123
ssl.key.password=kafka123
ssl.truststore.location=/opt/kafka/conf/certs/kafka.truststore
ssl.truststore.password=kafka123
ssl.client.auth=required

log.dirs=./logs/kafka-0

default.replication.factor=3
num.partitions=8
min.insync.replicas=2
auto.create.topics.enable=false
broker.rack=us-west2-a
queued.max.requests=1000
auto.leader.rebalance.enable=true

zookeeper.connect=localhost:2181
delete.topic.enable=true
compression.type=producer
message.max.bytes=65536
replica.lag.time.max.ms=5000
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
num.recovery.threads.per.data.dir=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connection.timeout.ms=6000
```

## ***ACTION*** - EDIT `config/server-0.properties` and follow instructions

#### ~/kafka-training/lab8.2/solution/config/server-1.properties
```sh
broker.id=1

listeners=SASL_PLAINTEXT://localhost:9093,SASL_SSL://localhost:10093
sasl.mechanism.inter.broker.protocol=GSSAPI
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka
security.inter.broker.protocol=SASL_PLAINTEXT

ssl.keystore.location=/opt/kafka/conf/certs/kafka.keystore
ssl.keystore.password=kafka123
ssl.key.password=kafka123
ssl.truststore.location=/opt/kafka/conf/certs/kafka.truststore
ssl.truststore.password=kafka123
ssl.client.auth=required

log.dirs=./logs/kafka-1

default.replication.factor=3
num.partitions=8
min.insync.replicas=2
auto.create.topics.enable=false
broker.rack=us-west2-a
queued.max.requests=1000
auto.leader.rebalance.enable=true

zookeeper.connect=localhost:2181
delete.topic.enable=true
compression.type=producer
message.max.bytes=65536
replica.lag.time.max.ms=5000
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
num.recovery.threads.per.data.dir=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connection.timeout.ms=6000
```

## ***ACTION*** - EDIT `config/server-1.properties` and follow instructions

#### ~/kafka-training/lab8.2/solution/config/server-2.properties
```sh
broker.id=2

listeners=SASL_PLAINTEXT://localhost:9094,SASL_SSL://localhost:10094
sasl.mechanism.inter.broker.protocol=GSSAPI
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka
security.inter.broker.protocol=SASL_PLAINTEXT

ssl.keystore.location=/opt/kafka/conf/certs/kafka.keystore
ssl.keystore.password=kafka123
ssl.key.password=kafka123
ssl.truststore.location=/opt/kafka/conf/certs/kafka.truststore
ssl.truststore.password=kafka123
ssl.client.auth=required

log.dirs=./logs/kafka-2

default.replication.factor=3
num.partitions=8
min.insync.replicas=2
auto.create.topics.enable=false
broker.rack=us-west2-a
queued.max.requests=1000
auto.leader.rebalance.enable=true

zookeeper.connect=localhost:2181
delete.topic.enable=true
compression.type=producer
message.max.bytes=65536
replica.lag.time.max.ms=5000
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
num.recovery.threads.per.data.dir=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connection.timeout.ms=6000
```

## ***ACTION*** - EDIT `config/server-2.properties` and follow instructions

## SASL Kerberos: Configuring Clients for SASL Kerberos

Sets the connection protocol to ***SASL_SSL***, encrypt with ***SSL***, authenticate with ***SASL***. <br>
Sets the service name to ***Kafka***. <br>
Sets the `sasl.mechanism` to ***Kerberos*** (GSSAPI).

#### ~/kafka-training/lab8.2/solution/src/main/java/com/fenago/kafka/consumer/ConsumerUtil.java
```java
package com.fenago.kafka.consumer;

import com.fenago.kafka.model.StockPrice;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConsumerUtil {

    private static Consumer<String, StockPrice> createConsumer(
            final String bootstrapServers, final String clientId ) {

        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BROKERS);

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put("sasl.kerberos.service.name", "kafka");
        props.put("sasl.mechanism", "GSSAPI");

        props.put("ssl.truststore.location", "/opt/kafka/conf/certs/kafka.truststore");
        props.put("ssl.truststore.password", "kafka123");
        props.put("ssl.keystore.location", "/opt/kafka/conf/certs/kafka.keystore");
        props.put("ssl.keystore.password", "kafka123");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "StockPriceConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StockDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        return new KafkaConsumer<>(props);
    }
...
}
```

## ***ACTION*** - EDIT `lab8.2/solution/src/main/java/com/fenago/kafka/consumer/ConsumerUtil.java` and follow instructions

## Kafka support multiple SASL Providers

Kafka supports more than one SASL provider.

#### ~/kafka-training/lab8.2/solution/jaas/kafka_broker_jaas.conf
```sh
KafkaServer {
  org.apache.kafka.common.security.scram.ScramLoginModule required
  username="admin"
  password="kafka123";

  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="admin-secret";
  user_admin="foobar"
  user_alice="barbaz";
};
```

## ***ACTION*** - EDIT `lab8.2/solution/jaas/kafka_broker_jaas.conf` and follow instructions

## Modifying SASL mechanism in a Running Cluster

SASL mechanism can be modified in a running cluster using the following sequence:

**Enable new SASL mechanism, add mechanism sasl.enabled.mechanisms in Broker Config server.properties.** <br>
*Update JAAS config file to include both mechanisms as describe.* <br>
**Bounce one Kafka Broker at a time.** <br>
*Restart clients using new mechanism.* <br>
**Change mechanism of inter-broker communication (if this is required), set sasl.mechanism.inter.broker.protocol in Broker Config server.properties to the new mechanism and bounce Kafka Brokers one at a time.** <br>
*Remove old mechanism (if this is required), remove old mechanism from sasl.enabled.mechanisms in Broker Config server.properties and remove entries for old mechanism from JAAS config file, and once again bounce Kafka Broker one at a time.* <br>

## Adding ACLs to users

#### ~/kafka-training/lab8.2/solution/config/server-0.properties
```sh
broker.id=0

authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=true

listeners=SASL_PLAINTEXT://localhost:9092,SASL_SSL://localhost:10092
sasl.mechanism.inter.broker.protocol=GSSAPI
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka
security.inter.broker.protocol=SASL_PLAINTEXT

ssl.keystore.location=/opt/kafka/conf/certs/kafka.keystore
ssl.keystore.password=kafka123
ssl.key.password=kafka123
ssl.truststore.location=/opt/kafka/conf/certs/kafka.truststore
ssl.truststore.password=kafka123
ssl.client.auth=required

log.dirs=./logs/kafka-0

default.replication.factor=3
num.partitions=8
min.insync.replicas=2
auto.create.topics.enable=false
broker.rack=us-west2-a
queued.max.requests=1000
auto.leader.rebalance.enable=true

zookeeper.connect=localhost:2181
delete.topic.enable=true
compression.type=producer
message.max.bytes=65536
replica.lag.time.max.ms=5000
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
num.recovery.threads.per.data.dir=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connection.timeout.ms=6000
```

## ***ACTION*** - EDIT `config/server-0.properties` and follow instructions

#### ~/kafka-training/lab8.2/solution/bin/create-acl.sh
```sh
#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-acls.sh \
    --authorizer-properties zookeeper.connect=localhost:2181 \
    --add --allow-principal User:stocks_consumer \
    --allow-host 10.0.1.11 --allow-host 198.51.100.1 \
    --operation Read  --topic stock-prices

kafka/bin/kafka-acls.sh \
    --authorizer-properties zookeeper.connect=localhost:2181 \
    --add --allow-principal User:stocks_producer \
    --allow-host 10.0.1.11 --allow-host 198.51.100.1 \
    --operation Write  --topic stock-prices
```

***--allow-principal*** (Configure the user who connects to broker) <br>
***--allow-host*** (Configure the host who connects to broker) <br>
***--operation*** (Write to producers or Read to consumers) <br>
***--topic*** (Configure the topic)

## ***ACTION*** - EDIT `bin/create-acl.sh` and follow instructions

## Run the lab

## ***ACTION*** - RUN ZooKeeper and three Kafka Brokers (scripts are under bin for ZooKeeper and Kafka Brokers).
## ***ACTION*** - RUN ConsumerBlueMain from the IDE
## ***ACTION*** - RUN StockPriceProducer from the IDE

## Expected results
You should be able to send records from the producer to the broker
and read records from the consumer to the broker using SASL.

