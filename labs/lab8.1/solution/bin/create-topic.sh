#!/usr/bin/env bash

cd ~/kafka-training

## Create a new Topic
kafka/bin/kafka-topics.sh \
    --create \
    --zookeeper localhost:2181 \
    --replication-factor 3 \
    --partitions 3 \
    --topic stock-prices \
    --config min.insync.replicas=1 \
    --config retention.ms=60000

    #--config unclean.leader.election.enable=true \
    #--config min.insync.replicas=2 \
    #--config compression.type=producer \
    #--config cleanup.policy=compact \





