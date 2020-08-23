#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

# Verify executing reassignment plan
kafka/bin/kafka-reassign-partitions.sh --verify \
    --reassignment-json-file "$CONFIG/assignment-plan.json" \
    --zookeeper localhost:2181

