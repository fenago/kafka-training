#!/usr/bin/env bash

cd ~/kafka-training

# List existing topics
kafka/bin/kafka-replica-verification.sh  \
    --report-interval-ms 5000 \
    --topic-white-list  "stock-prices.*" \
    --broker-list localhost:9092,localhost:9093,localhost:9094

