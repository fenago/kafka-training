#!/usr/bin/env bash
cd ~/kafka-training
SCRAM_CONFIG='SCRAM-SHA-256=[iterations=8192,password=kafka123]'
SCRAM_CONFIG="$SCRAM_CONFIG,SCRAM-SHA-512=[password=kafka123]"

# TODO create stocks_consumer user
# TODO create stocks_producer user
# TODO create admin user

# HINT kafka/bin/kafka-configs.sh \
#    --alter --add-config "$SCRAM_CONFIG" \
#    --entity-type users --entity-name ???
#    --zookeeper localhost:2181 \

