#!/usr/bin/env bash
cd ~/kafka-training
BOOTSTRAP_SERVERS="localhost:9092,localhost:9093"
kafka/bin/kafka-consumer-groups.sh --describe  \
    --bootstrap-server "$BOOTSTRAP_SERVERS"    \
    --group StockPriceConsumer


