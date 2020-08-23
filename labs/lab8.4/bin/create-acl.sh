#!/usr/bin/env bash
cd ~/kafka-training

kafka/bin/kafka-acls.sh \
    --authorizer-properties zookeeper.connect=localhost:2181 \
    --add --allow-principal User:stocks_consumer \
    --allow-host 10.0.1.11 --allow-host 198.51.100.1 \
    --operation Read  --topic stock-prices

kafka/bin/kafka-acls.sh \
    --authorizer-properties zookeeper.connect=localhost:2181 \
    --add --allow-principal User:stocks_producer \
    --allow-host 10.0.1.11 --allow-host 198.51.100.1 \
    --operation Write  --topic stock-prices


