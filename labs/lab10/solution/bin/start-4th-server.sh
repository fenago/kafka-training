#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training


## Run Kafka for 4th Server
kafka/bin/kafka-server-start.sh \
    "$CONFIG/server-3.properties"





