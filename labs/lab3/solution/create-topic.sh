#!/usr/bin/env bash
cd ~/kafka-training
kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 \
--replication-factor 1 --partitions 13 --topic my-example-topic2

kafka/bin/kafka-topics.sh --list --zookeeper localhost:2181
