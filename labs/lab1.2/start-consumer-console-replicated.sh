#!/usr/bin/env bash
cd ~/kafka-training


## TODO FIRST RUN LIKE THIS

## TODO Run kafka-console-consumer.sh
# HINT -  kafka/bin/kafka-console-consumer.sh \
## TODO Specify the three Kafka servers that we ran earlier as bootstrap servers
# HINT --bootstrap-server localhost:9094,localhost:9092 \
## TODO Pass the name of the topic that we just created
# HINT --topic my-failsafe-topic \
## TODO consume from the start of the topic log
# HINT --from-beginning

## STOP STOP STOP

## TODO Don't add this until instructed to.
--consumer-property group.id=mygroup \


