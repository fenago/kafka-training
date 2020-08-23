#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

## TODO set up JASS config
## REMEMBER TO cp -R resources/opt/kafka/conf/security /opt/kafka/conf/ after done with JAAS config

## HINT export KAFKA_JAAS_FILE="/opt/kafka/conf/security/kafka_broker_jaas.conf"
## HINT export KAFKA_OPTS="-Djava.security.auth.login.config=$KAFKA_JAAS_FILE"

## Run Kafka for 2nd Server
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-1.properties"


