#!/usr/bin/env bash

cd ~/kafka-training

kafka/bin/kafka-topics.sh \
    --create \
    --zookeeper localhost:2181 \
    --replication-factor 1 \
    --partitions 3 \
    --topic stock-prices \
    --config min.insync.replicas=1


    #--config unclean.leader.election.enable=true \
    #--config min.insync.replicas=2 \
    #--config compression.type=producer \
    #--config cleanup.policy=compact \
    #--config retention.ms=60000




