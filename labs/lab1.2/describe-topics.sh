#!/usr/bin/env bash

cd ~/kafka-training

#TODO DESCRIBE my-failsafe-topic
kafka/bin/kafka-topics.sh --describe --topic my-failsafe-topic --zookeeper localhost:2181
# TODO PASS NAME OF THE TOPIC
# HINT    --topic my-failsafe-topic \
# TODO PASS ZOOKEEPER ADDRESS
# HINT    --zookeeper localhost:2181



