#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training
## Run Kafka Mirror Maker: Mirror 2nd Cluster to 3rd Cluster
kafka/bin/kafka-mirror-maker.sh \
    --consumer.config "$CONFIG/mm-consumer-2nd.properties" \
    --producer.config "$CONFIG/mm-producer-3rd.properties" \
    --whitelist ".*"
