: ASSUMES kafka/bin in in the classpath

:Create input topic
call kafka-topics --create ^
    --replication-factor 1 ^
    --partitions 1 ^
    --topic word-count-input ^
    --zookeeper localhost:2181

:Create output topic
call kafka-topics --create ^
    --replication-factor 1 ^
    --partitions 1 ^
    --topic word-count-output ^
    --zookeeper localhost:2181

:List created topics
call kafka-topics --list --zookeeper localhost:2181
