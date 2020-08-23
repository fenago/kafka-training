#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training

## Run Kafka for 2nd Server
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-1.properties"


