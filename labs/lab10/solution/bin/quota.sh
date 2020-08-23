#!/usr/bin/env bash

cd ~/kafka-training

## Add limit for stock_analyst user running as clientId stockConsumer
kafka/bin/kafka-configs.sh --alter \
    --zookeeper localhost:2181 \
    --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048' \
    --entity-type users \
    --entity-name stock_analyst \
    --entity-type clients \
    --entity-name stockConsumer

