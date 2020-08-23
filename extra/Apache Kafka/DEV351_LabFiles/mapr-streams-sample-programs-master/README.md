# Sample Programs for MapR Streams

This project provides a simple but realistic example of a Kafka
producer and consumer compatible with MapR Streams. 


* the topic names are using the [MapR Streams](http://maprdocs.mapr.com/51/index.html#MapR_Streams/concepts.html) naming convention `/stream:topic`
* the application is executed in the context of a MapR Client and its dependencies.


## Pre-requisites
To start, you need to get a MapR 5.1 running. You can install your own cluster or download a sandbox.

### Step 1: Create the stream

A *stream* is a collection of topics that you can manage together for security, default number or partitions, and time to leave for the messages.

Run the following command on your MapR cluster:

```
$ maprcli stream create -path /user/user01/sample-stream
```

By default the produce and consume topic permission are defaulted to the creator of the streams, the unix user you are using to run the `maprcli` command.

It is possible to configure the permission by editing the streams, for example to make all the topic available to anybody (public permission) you can run the following command:

```
maprcli stream edit -path /user/user01/sample-stream -produceperm p -consumeperm p -topicperm p
```

This is useful for this example since we want to run the producer and consumer from remote computers too.

### Step 2: Create the topics

We need two topics for the example program, that are also created with the `maprcli` tool
```
$ maprcli stream topic create -path /user/user01/sample-stream  -topic fast-messages
$ maprcli stream topic create -path /user/user01/sample-stream  -topic summary-markers
```

These can be listed
```
$ maprcli stream topic list -path /user/user01/sample-stream
topic            partitions  logicalsize  consumers  maxlag  physicalsize
fast-messages    1           0            0          0       0
summary-markers  1           0            0          0       0
```

Note that the program will automatically create the topic if it does not already exist.


### Step 3: Compile and package up the example programs

Go back to the directory where you have the example programs and
compile and build the example programs.

```
$ cd ..
$ mvn package
...
```

The project create a jar with all external dependencies ( `./target/mapr-streams-examples-1.0-jar-with-dependencies.jar` )

### Step 4: Run the example producer

You can install the [MapR Client](http://maprdocs.mapr.com/51/index.html#AdvancedInstallation/SettingUptheClient-client_26982445-d3e146.html) and run the application locally,
or copy the jar file on your cluster (any node).

For example copy the program to your server using scp:

```
scp ./target/mapr-streams-examples-1.0-jar-with-dependencies.jar user01@<YOUR_MAPR_CLUSTER>:/users/user01
```

The producer will send a large number of messages to `/user/user01/sample-stream:fast-messages`
along with occasional messages to `/user/user01/sample-stream:summary-markers`. Since there isn't
any consumer running yet, nobody will receive the messages. 

Any MapR Streams application need the MapR Client library to be executed, for this you just have to add them to the application classpath. 
The classpath is available using the command `/opt/mapr/bin/mapr classpath`. So you can run the application using the following command


```
$ java -cp `mapr classpath`:./mapr-streams-examples-1.0-jar-with-dependencies.jar com.mapr.examples.Run producer
Sent msg number 0
Sent msg number 1000

```


### Step 5: Start the example consumer

In another window you can run the consumer using the following command:

```
$ java -cp `mapr classpath`:./mapr-streams-examples-1.0-jar-with-dependencies.jar com.mapr.examples.Run consumer
1 messages received in period, latency(min, max, avg, 99%) = 20352, 20479, 20416.0, 20479 (ms)
1 messages received overall, latency(min, max, avg, 99%) = 20352, 20479, 20416.0, 20479 (ms)
1000 messages received in period, latency(min, max, avg, 99%) = 19840, 20095, 19968.3, 20095 (ms)

```

Note that there is a latency listed in the summaries for the message batches. 
This is because the consumer wasn't running when the message were sent to Kafka and thus 
it is only getting them much later, long after they were sent.

The consumer should, however, gnaw its way through the backlog pretty quickly, 
however and the per batch latency should be shorter by the end of the run than at the beginning. 
If the producer is still running by the time the consumer catches up, the latencies will probably 
drop into the single digit millisecond range.

### Monitoring your topics 

At any time you can use the `maprcli` tool to get some information about the topic, for example:

```
$ maprcli stream topic info -path /user/user01/sample-stream -topic fast-messages -json
```
`-json` is just use to get the topic information as a JSON document.


## Cleaning Up

When you are done playing, you can delete the stream, and all associated topic using the following command:
```
$ maprcli stream delete -path /user/user01/sample-stream
```



## From Apache Kafka to MapR Streams

1. The topics have move from `"fast-messages"` to `"/user/user01/sample-stream:fast-messages"` and `"summary-markers"` to `"/user/user01/sample-stream:summary-markers"`
2. The [producer](http://maprdocs.mapr.com/51/index.html#MapR_Streams/configuration_parameters_for_producers.html) and [consumer](http://maprdocs.mapr.com/51/index.html#MapR_Streams/configuration_parameters_for_consumers.html) configuration parameters that are not used by MapR Streams are automatically ignored
3. The producer and Consumer applications are executed with the dependencies of a MapR Client not Apache Kafka.

That's it!


## Credits
Note that this example was derived in part from the documentation provided by the Apache Kafka project. We have 
added short, realistic sample programs that illustrate how real programs are written using Kafka.  
