#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training


## cp -R resources/opt/kafka/conf/security /opt/kafka/conf/
export KAFKA_JAAS_FILE="/opt/kafka/conf/security/kafka_broker_jaas.conf"
export KAFKA_OPTS="-Djava.security.auth.login.config=$KAFKA_JAAS_FILE"

## Run Kafka for 1st Server
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-0.properties"


