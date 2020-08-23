#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

## Run ZooKeeper for 1st Cluster
kafka/bin/zookeeper-server-start.sh \
   "$CONFIG/zookeeper-0.properties" &


## Run Kafka for 1st Cluster
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-0.properties"







