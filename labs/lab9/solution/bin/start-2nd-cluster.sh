#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training


## Run ZooKeeper for 2nd Cluster
kafka/bin/zookeeper-server-start.sh \
   "$CONFIG/zookeeper-1.properties" &


## Run Kafka for 2nd Cluster
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-1.properties"


