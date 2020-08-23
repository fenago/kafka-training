#!/usr/bin/env bash

cd ~/kafka-training


kafka/bin/kafka-configs.sh --describe  \
    --zookeeper localhost:2181 \
    --entity-type users --entity-name stocks_consumer

kafka/bin/kafka-configs.sh --describe  \
    --entity-type users --entity-name stocks_producer \
    --zookeeper localhost:2181 \

kafka/bin/kafka-configs.sh --describe  \
    --entity-type users --entity-name admin \
    --zookeeper localhost:2181 \

