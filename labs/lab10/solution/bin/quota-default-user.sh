#!/usr/bin/env bash

cd ~/kafka-training

## Add limit to default user
kafka/bin/kafka-configs.sh --alter \
    --zookeeper localhost:2181 \
    --add-config 'producer_byte_rate=512,consumer_byte_rate=512' \
    --entity-type users --entity-default

