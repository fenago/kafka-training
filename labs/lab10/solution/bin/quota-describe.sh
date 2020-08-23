#!/usr/bin/env bash

cd ~/kafka-training

## Describe a quota
kafka/bin/kafka-configs.sh --describe \
    --zookeeper localhost:2181 \
    --entity-type users \
    --entity-name stock_analyst \
    --entity-type clients \
    --entity-name stockConsumer

