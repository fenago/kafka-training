#!/usr/bin/env bash
cd ~/kafka-training

## TODO create a consumer for the output
# HINT - kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
## TODO point to the correct topic
# HINT -     --topic word-count-output \
## TODO all entries since the beginning
# HINT -     --from-beginning \
## TODO set the formatter
# HINT -     --formatter kafka.tools.DefaultMessageFormatter \
## TODO yes, print key
# HINT -     --property print.key=true \
## TODO yes, print value
# HINT -     --property print.value=true \
## TODO key is a string, deserializer is the StringDeserializer
# HINT -     --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
## TODO value is a number (long), deserializer is the LongDeserializer
# HINT -     --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
