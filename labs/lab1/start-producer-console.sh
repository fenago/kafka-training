#!/usr/bin/env bash
cd ~/kafka-training


## TODO Run the producer console.
kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-topic
## TODO pass the borker list. Recall the broker runs on port 9092.
# HINT: --broker-list localhost:9092 \
## TODO Pass the topic that we just created.
# HINT: --topic my-topic


