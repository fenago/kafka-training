#!/usr/bin/env bash

cd ~/kafka-training

## Alter the topic
kafka/bin/kafka-topics.sh --alter \
    --zookeeper localhost:2181 \
    --partitions 9 \
    --topic stock-prices \
    --config min.insync.replicas=3 \
    --delete-config retention.ms

