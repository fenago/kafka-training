#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

# Execute reassignment plan
kafka/bin/kafka-reassign-partitions.sh --execute \
    --reassignment-json-file "$CONFIG/assignment-plan.json" \
    --throttle 100000 \
    --zookeeper localhost:2181

