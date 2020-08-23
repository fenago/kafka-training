package example.sequence;

/*
 * 
 * to run
 *  java -cp `mapr classpath`:ms-lab-1.0.jar example.sequence.GenNumbers send numsequences=100
 *  java -cp `mapr classpath`:ms-lab-1.0.jar example.sequence.GenNumbers read
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class GenNumbers {

    static String baseKey;
    static boolean sendMessages;
    static long numMessages;
    static long numSequences;
    static long maxWaitBeforeExit;
    static String topic;
    static Date startTime, endTime;
    static long totalMessages = 0;
    static boolean sync = false;
    static boolean nobuf = false;

    static HashMap<String, SequenceInfo> sequences = new HashMap<String, SequenceInfo>();

    static public void createSequence() {
        int newseqnum = sequences.size() + 1;
        String key = baseKey + String.format("%06d", newseqnum);
        SequenceInfo seq = new SequenceInfo(key);
        sequences.put(key, seq);
    }

    static public SequenceInfo updateSequence(String key, long num) {
        SequenceInfo seq;
        seq = sequences.get(key);
        if (seq == null) {
            System.out.println("Found new sequence: " + key);
            seq = new SequenceInfo(key);
            sequences.put(key, seq);
        }

        seq.add(num);

        return seq;
    }

    public static void main(String[] args) throws Exception {

        String directionStr = args[0];
        if ("send".equals(directionStr)) {
            sendMessages = true;
        } else {
            sendMessages = false;
        }

        Properties argprops = new Properties();
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            String[] splitarg = arg.split("=", 2);
            String prop = splitarg[0].toLowerCase();
            String value = splitarg[1].toLowerCase();

            argprops.put(prop, value);
        }
        baseKey = argprops.getProperty("basekey", "key");
        numMessages = getPropAsNum(argprops, "nummessages", "1000");
        numSequences = getPropAsNum(argprops, "numsequences", "1");
        maxWaitBeforeExit = getPropAsNum(argprops, "maxwait", "10");
        topic = "/user/user01/pump:numbers";
        sync = "true".equals(argprops.getProperty("sync", "false"));
        nobuf = "true".equals(argprops.getProperty("nobuf", "false"));

        if (sendMessages) {
            System.out.println("Sending " + numMessages + " to topic " + topic);
            System.out.println("Will generate " + numSequences + " sequence(s)"
                    + " with base key of " + baseKey);
            if (sync) {
                System.out.println("Sending mode is synchronous and buffering is " + !nobuf);
            }
        } else {
            System.out.println("Getting messages from topic " + topic
                    + " with a maximum wait of " + maxWaitBeforeExit
                    + " seconds");
        }

        Properties kprops = new Properties();

        // prevent parallel producing threads (avoids out of order messages)
        kprops.put("marlin.parallel.flushers.per.partition", "false");

        // cause consumers to start at beginning of topic on first read when no saved cursor
        kprops.put("auto.offset.reset", "earliest");

        //used by Streams to display info in several places
        kprops.put("client.id", "testclient");

        // consumer group for cursor tracking and topic sharing
        kprops.put("group.id", "myteam");
        System.out.println("Using group.id = myteam");

        if (nobuf) {
            kprops.put("marlin.buffer.max.time.ms", 0);
        }

        startTime = new Date();
        if (sendMessages) {

            // create sequence generators
            for (int i = 0; i < numSequences; i++) {
                createSequence();
            }

            Producer<String, String> producer = new KafkaProducer<String, String>(
                    kprops,
                    new org.apache.kafka.common.serialization.StringSerializer(),
                    new org.apache.kafka.common.serialization.StringSerializer());

            System.out.print("sending...");

            // now send messages the number of times requested
            for (int cnt = 0; cnt < numMessages; cnt++) {
                for (SequenceInfo seq : sequences.values()) {
                    String key = seq.key;
                    long num = seq.genNext();

                    ProducerRecord<String, String> rec = new ProducerRecord<String, String>(
                            topic, key, Long.toString(num));
                    Future<RecordMetadata> future = producer.send(rec);
                    if (sync) {
                        future.get();
                    }
                    totalMessages++;
                }
                if (cnt % 1000 == 0) {
                    System.out.print(".");
                }
            }
            System.out.println();

            producer.close();
            endTime = new Date();

            System.out
                    .println("All messages sent. Here is summary sequence info: ");
            printSeq(sequences);

        } else {

            Consumer<String, String> consumer = new KafkaConsumer<String, String>(
                    kprops,
                    new org.apache.kafka.common.serialization.StringDeserializer(),
                    new org.apache.kafka.common.serialization.StringDeserializer());

            consumer.subscribe(Pattern.compile(topic), new SaveCursor(consumer));

            long waitTime = maxWaitBeforeExit * 1000;
            while (waitTime > 0) {
				//System.out.println("Wait 1 second for messages (" + waitTime
                //		/ 1000 + " seconds left)");
                ConsumerRecords<String, String> msg = consumer.poll(1000);
                if (msg.count() == 0) {
                    System.out.println("No messages after 1 second wait. waitTime " + waitTime / 1000 + " seconds left");
                    waitTime = waitTime - 1000;
                } else {
                    System.out.print(" " + msg.count());
                    totalMessages += msg.count();
                    for (ConsumerRecord<String, String> record : msg) {
                        long num = Long.parseLong(record.value());
                        updateSequence(record.key(), num);
                    }
                    waitTime = maxWaitBeforeExit * 1000;
                }
            }
            System.out.println();

            // System.out.println("Calling commit");
            consumer.commitSync();
			// consumer.commit(CommitType.ASYNC);

			// System.out.println("Called commit");
            // System.out.println("Calling consumer.close()");
            consumer.close();
            endTime = new Date();

            System.out
                    .println("All messages consumed. Here is summary sequence info: ");
            printSeq(sequences);
        }

        if (totalMessages > 0) {
			// time spent, but subtract off the final wait time. Do NOT subtract
            // off earlier waits
            // as they indicate that this client was waiting for work that
            // wasn't there for some reason.
            long secSpent = (endTime.getTime() - startTime.getTime()) / 1000;
            if (!sendMessages) {
                secSpent -= maxWaitBeforeExit;
            }
            System.out.println("Processed " + totalMessages + " messages in "
                    + secSpent + " seconds.");
            if (secSpent > 1) {
                float rate = totalMessages / secSpent / 1000;
                System.out.println("Rate is then " + rate
                        + " thousand messages/second");
            }
        }
    }

    static private void printSeq(HashMap<String, SequenceInfo> sequences) {
        List<String> keylist = new ArrayList<String>(sequences.keySet());
        Collections.sort(keylist);
        for (String key : keylist) {
            SequenceInfo seq = sequences.get(key);
            System.out.println(seq);
        }
    }

    static private long getPropAsNum(Properties p, String key, String def) {
        String res = p.getProperty(key, def);
        return new Long(res).longValue();
    }
}
