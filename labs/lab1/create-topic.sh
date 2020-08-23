#!/usr/bin/env bash

cd ~/kafka-training

# TODO Create a topic
# TODO Pass topic name my-topic, number of partitions 13 and replication factor 1.
# ZooKeeper is running on port 2181 be sure to pass zookeeper's address.

kafka/bin/kafka-topics.sh

# HINT --create --zookeeper localhost:2181 \
# --replication-factor 1 --partitions 13 --topic my-topic


