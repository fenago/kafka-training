#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

# TODO set up JAAS file for ZooKeeper to /opt/kafka/conf/security/zookeeper_jaas.conf
# HINT export KAFKA_JAAS_FILE="/opt/kafka/conf/security/zookeeper_jaas.conf"
# HINT export KAFKA_OPTS="-Djava.security.auth.login.config=$KAFKA_JAAS_FILE"

## Run ZooKeeper for 1st Cluster
kafka/bin/zookeeper-server-start.sh \
   "$CONFIG/zookeeper.properties"






