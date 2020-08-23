#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training
## Run Kafka Mirror Maker: Mirror 1st Cluster to 2nd Cluster
kafka/bin/kafka-mirror-maker.sh \
    --consumer.config "$CONFIG/mm-consumer-1st.properties" \
    --producer.config "$CONFIG/mm-producer-2nd.properties" \
    --whitelist ".*"

