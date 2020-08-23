#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

## Run ZooKeeper for 3rd Cluster
kafka/bin/zookeeper-server-start.sh \
   "$CONFIG/zookeeper-2.properties" &


## Run Kafka for 3rd Cluster
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-2.properties"





