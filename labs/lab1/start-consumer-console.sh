#!/usr/bin/env bash
cd ~/kafka-training


## TODO Run the consumer console.
kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-topic
## TODO pass the broker address.
# HINT:    --bootstrap-server localhost:9092 \
## TODO pass the topic that you just created.
# HINT:    --topic my-topic \
## TODO pass the from begining directive so that it reads from the start of the Kafka topic.
# HINT: --from-beginning


