#!/usr/bin/env bash
cd ~/kafka-training
SCRAM_CONFIG='SCRAM-SHA-256=[iterations=8192,password=kafka123]'
SCRAM_CONFIG="$SCRAM_CONFIG,SCRAM-SHA-512=[password=kafka123]"

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name stocks_consumer \
    --bootstrap-server localhost:9092,localhost:9093,localhost:9094 \

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name stocks_producer \
    --bootstrap-server localhost:9092,localhost:9093,localhost:9094 \

kafka/bin/kafka-configs.sh \
    --alter --add-config "$SCRAM_CONFIG" \
    --entity-type users --entity-name admin \
    --bootstrap-server localhost:9092,localhost:9093,localhost:9094 \

