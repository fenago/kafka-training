#!/usr/bin/env bash
cd ~/kafka-training

## TODO create input topic
## TODO set the replication-factor to 1
## TODO set the partitions to 1
## TODO set the topic
# HINT - kafka/bin/kafka-topics.sh --create \
# HINT -     --replication-factor 1 \
# HINT -     --partitions 1 \
# HINT -     --topic word-count-input \
# HINT -     --zookeeper localhost:2181

## TODO create output topic
## TODO set the replication-factor to 1
## TODO set the partitions to 1
## TODO set the topic
# HINT - kafka/bin/kafka-topics.sh --create \
# HINT -     --replication-factor 1 \
# HINT -     --partitions 1 \
# HINT -     --topic word-count-output \
# HINT -     --zookeeper localhost:2181

## List created topics
kafka/bin/kafka-topics.sh --list \
    --zookeeper localhost:2181
