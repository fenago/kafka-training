#!/usr/bin/env bash
cd ~/kafka-training

## Create input topic
kafka/bin/kafka-topics.sh --create \
    --replication-factor 1 \
    --partitions 1 \
    --topic word-count-input \
    --zookeeper localhost:2181

## Create output topic
kafka/bin/kafka-topics.sh --create \
    --replication-factor 1 \
    --partitions 1 \
    --topic word-count-output \
    --zookeeper localhost:2181

## List created topics
kafka/bin/kafka-topics.sh --list \
    --zookeeper localhost:2181
