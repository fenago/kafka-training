#!/usr/bin/env bash

cd ~/kafka-training

kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 13 --topic my-failsafe-topic
## TODO Pass zookeeper address
# HINT --zookeeper localhost:2181 \
## TODO Set replication factor to 3
# HINT --replication-factor 3 \
## TODO use 13 partitions
# HINT --partitions 13 \
## TODO Call this replicated topic my-failsafe-topic
# HINT --topic my-failsafe-topic



