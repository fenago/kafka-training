# API Tutorial

You can use this sample code to learn more about the Apache Kafka 0.9 API, this code should run without change on the MapR Streams


## Create topics for sample

maprcli stream create -path /user/user01/pump
maprcli stream edit -path /user/user01/pump -produceperm u:user01 -consumeperm u:user01 -topicperm u:user01
maprcli stream topic create -path /user/user01/pump -topic input -partitions 3
maprcli stream topic create -path /user/user01/pump -topic alert -partitions 3


## Simple Producer and Consumer

 java -cp ms-lab-1.0.jar:`mapr classpath` example.simple.Producer /user/user01/pump:input

 java -cp ms-lab-1.0.jar:`mapr classpath` example.simple.Consumer /user/user01/pump:input 0

 java -cp ms-lab-1.0.jar:`mapr classpath` example.simple.Consumer /user/user01/pump:input 0

 java -cp ms-lab-1.0.jar:`mapr classpath` example.simple.Consumer /user/user01/pump:input 1

 java -cp ms-lab-1.0.jar:`mapr classpath` example.simple.Consumer /user/user01/pump:input 1


## Partitions

 java -cp ms-lab-1.0.jar:`mapr classpath` example.partition.Producer /user/user01/pump:input

 java -cp ms-lab-1.0.jar:`mapr classpath` example.partition.Consumer /user/user01/pump:input

 java -cp ms-lab-1.0.jar:`mapr classpath` example.partition.Consumer /user/user01/pump:input
```

## Consumer Groups
```sh
 java -cp ms-lab-1.0.jar:`mapr classpath` example.group.Producer /user/user01/pump:input

 java -cp ms-lab-1.0.jar:`mapr classpath` example.group.Consumer /user/user01/pump:input group2

 java -cp ms-lab-1.0.jar:`mapr classpath` example.group.Consumer /user/user01/pump:input group1

 java -cp ms-lab-1.0.jar:`mapr classpath` example.group.Consumer /user/user01/pump:input group1
```

## Sequence

java -cp ms-lab-1.0.jar:`mapr classpath` example.sequence.Sample send /user/user01/pump:input 5
java -cp ms-lab-1.0.jar:`mapr classpath` example.sequence.Sample get /user/user01/pump:input 5