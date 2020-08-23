#!/usr/bin/env bash
cd ~/kafka-training

# List existing topics
kafka/bin/kafka-topics.sh \
    --describe \
    --topic stock-prices \
    --zookeeper localhost:2181



