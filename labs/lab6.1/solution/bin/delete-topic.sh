#!/usr/bin/env bash
cd ~/kafka-training
kafka/bin/kafka-topics.sh \
    --delete \
    --zookeeper localhost:2181 \
    --topic stock-prices





