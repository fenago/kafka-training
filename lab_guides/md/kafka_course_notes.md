## Kafka Course May 2018

Check back at the [course notes page for updates, notes, and answer to questions during the course](https://gist.github.com/RichardHightower/f36f71ac139cb4d88c4f5f5f6eac58b0). 


Review the course outline: [Course outline](http://cloudurable.com/kafka-training-details/index.html)

## Files 
* The ***course files*** can be found [here](https://s3-us-west-2.amazonaws.com/kafka-course-may-2018/kafka-training.zip).
* The Linux VirtualBox OVA image from [here](https://s3-us-west-2.amazonaws.com/kafka-course-may-2018/kafka-class.ova). 

The ***Virtual Box image*** and the ***course files zip*** has ***Kafka*** and ***Confluent (Schema Registry, etc.)*** already installed. 


## Source, Kafka, Labs, Zip File
The zip file should be exracted to `~/` such that it creates a directory called `~/kafka-training`. 
The VirtualBox image already has the zip file extracted in the right location. 
You should use the Kafka VirtualBox image for all labs. 
[Disclaimer about not using Virtual Box image](https://gist.github.com/RichardHightower/4f7e0a1dd90c37ecdd40b340408223a9).

## Lab files
Every lab folder has a `README.md` with instructions on how to complete the lab, and a `solution` folder with a working version of the lab for reference. Please use the README.md and solution folder as a guide to completing the labs. 

## System Requirements
The development computer will need 16 GB of RAM, and should have 2.5 GHz Intel Core i7 or better for the least amount of frustration. Most developer computers will have this much or more. You should have 30 GB of free disk space. 
You will need to run bash scripts. These have been tested under Linux and OSX. 

## Windows Users
We recommend using the Linux Virtual machine image if you are using Windows. We don't support running the labs on Windows direct or Windows batch files. You will need to install VirtualBox 5.1.x. We used VirtualBox 5.1.22 which is the latest as of July 17th, 2017. Use the provided image above, which has Java 1.8, Kafka, and Confluent Streams already installed. 
We can't stop the course to help install Kafka, Java, Gradle, Scala, etc. on 15+ developer boxes. Please use the image.


## OSX Users and Linux Users
You can just unzip the zip file under `~/` so that it creates a directory `~/kafka-training`. 
You need the Java 1.8 JDK and a Java IDE of your choice. The zip file is self-contained (other than the JDK) so the environment issues should be minimal. 

However, if you have environment problems, please use the VirutalBox image for the course instead. We used [VirtualBox 5.X.X](https://www.virtualbox.org/wiki/Downloads). Use the provided image above, which has IDEA, Java 1.8, Kafka, and Confluent Streams already installed. *There are thirty labs and 15 students (and four days) so we will not have a lot of time to sort out environment issues on 15 development boxes.*  The focus will be on Kafka not your environment. 

The VirtualBox image uses Ubuntu and it was installed via [these steps](https://gist.github.com/RichardHightower/bdfbd7abd7dcfd2c90b3580673997a93). You do not have to install. Just use the image.

[Go here to install VirtualBox](https://www.virtualbox.org/wiki/Downloads).
Once virtual box is installed, you can double click the OVA image file from above and the image will be installed. 
You may want to increase the RAM to 8GB and the number of processors used by the image to 4.

## Lab Instructions
The latest instructions for the labs can be found below. 



1. [Lab 1](https://gist.github.com/RichardHightower/37433e766e5915aae3048ade08b3db56) Basic Kafka 
1. [Lab 1.2](https://gist.github.com/RichardHightower/42c78c0764990a76b8ed4b20e479fe98) Understanding Failover
1. [Lab 2](https://gist.github.com/RichardHightower/7318bd2ddcd2b878cd92991eda417229) Writing a simple Java Producer
1. [Lab 3](https://gist.github.com/RichardHightower/9a709c4828f41a93118964f0ec8b3b34) Writing a simple Java Consumer
1. [Lab 5.1](https://gist.github.com/RichardHightower/d6648b2812d3eefb9bc46752c09c5ca6) Advanced Producers
1. [Lab 5.2](https://gist.github.com/RichardHightower/5708326f9052307d312f90a6fa189eda) Clean shutdown 
1. [Lab 5.3](https://gist.github.com/RichardHightower/87b1646895502d9c098a0e5610c8e622) Configuring Producer Durability
1. [Lab 5.4](https://gist.github.com/RichardHightower/b8ce014913da7c69166273bf486bc629) Metrics and Replication Verification
1. [Lab 5.5](https://gist.github.com/RichardHightower/313eb5b91ab8876690f9d87a20f6e3bd) Kafka Batching Records (compression)
1. [Lab 5.6](https://gist.github.com/RichardHightower/f97963127e8e2343180b5080ecca1c13) Retry, timeouts, in-flight, back-off
1. [Lab 5.7](https://gist.github.com/RichardHightower/2976b878ed48603fe85766af5f85ac78) Kafka  Interceptor (Optional)
1. [Lab 5.8](https://gist.github.com/RichardHightower/bbb6ea6f059cf757810ef470f5eddaf8) Kafka Custom Partitioner (Optional)
1. [Lab 6.1](https://gist.github.com/RichardHightower/a0e81201220714fd664c0e98b3b28471) Advanced Consumer 
1. [Lab 6.2](https://gist.github.com/RichardHightower/45620559570880b378788bce65848d5f) Position Consumer in Topic Log
1. [Lab 6.3](https://gist.github.com/RichardHightower/8411962423cdda40e09190646977b959) At-Most-Once and At-Least-Once Semantics
1. [Lab 6.4](https://gist.github.com/RichardHightower/dfec9a329c617f69e2e5835b218b01b9) Exactly-Once message semantics
1. [Lab 6.5](https://gist.github.com/RichardHightower/6ea515847dd63d524dfc855771ff70e6) Thread per consumer
1. [Lab 6.6](https://gist.github.com/RichardHightower/041c2c546e513a3481289ca16fe11cd4) Consumer with worker threads
1. [Lab 6.7](https://gist.github.com/RichardHightower/81a66e0f9822e9e74660deec10640d27) Priority queue with consumer.assign
1. [Lab 7.1](https://gist.github.com/RichardHightower/2f6ea2599ef66814668ee680ff9c0649) Avro
1. [Lab 7.2](https://gist.github.com/RichardHightower/dfa076b30fcae0d8b5672265c63a8bba) Schema Registry
1. [Lab 8.1](https://gist.github.com/RichardHightower/965b627ab7b67eb35c8fe7dc9e4fb710) Kafka Security SSL 
1. [Lab 8.3](https://gist.github.com/RichardHightower/bb989e89db38c092795ad108edd45b7d) Kafka Security SASL PLAIN 
1. [Lab 8.4](https://gist.github.com/RichardHightower/54a9ce6b2ff35645605711a0b8022fd7) Kafka Security SASL SCRAM 
1. [Lab 9](https://gist.github.com/RichardHightower/00fa622c4128bbf25a796029882a90aa) Kafka MirrorMaker 
1. Lab 10 DEMO walk through
1. [Lab Streams 1](https://gist.github.com/RichardHightower/aa762d85306a57bc7d8f32a78c651ac5) Kafka Streams Lab 1
1. [Lab Streams 2](https://gist.github.com/RichardHightower/69238682cceb43d4fb890224e8f1a52a) Kafka Streams Lab 2



