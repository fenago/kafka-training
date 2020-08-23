#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

# Generate Reassignment Plan
kafka/bin/kafka-reassign-partitions.sh --generate \
    --broker-list 0,1,2 \
    --topics-to-move-json-file "$CONFIG/move-topics.json" \
    --zookeeper localhost:2181 > "$CONFIG/assignment-plan.json"

