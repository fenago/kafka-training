#!/usr/bin/env bash
CONFIG=`pwd`/config
cd ~/kafka-training
kafka/bin/kafka-server-start.sh "$CONFIG/server-1.properties"

## TODO Run Kafka
# HINT: kafka/bin/kafka-server-start.sh \
## TODO Pass config file.
# HINT: "$CONFIG/server-?????.properties"


