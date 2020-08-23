package example.partition;

import java.util.Map;

import org.apache.kafka.common.Cluster;

/**
 *
 * topic - The topic name key - The key to partition on (or null if no key)
 * keyBytes - The serialized key to partition on( or null if no key) value - The
 * value to partition on or null valueBytes - The serialized value to partition
 * on or null cluster - The current cluster metadata
 */
public class DemoPartitioner implements org.apache.kafka.clients.producer.StreamsPartitioner {

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

    @Override
    public int partition(java.lang.String topic,
            java.lang.Object key,
            byte[] keyBytes,
            java.lang.Object value,
            byte[] valueBytes,
            int numPartitions) {
        System.out.println("DemoPartitioner  topic " + topic + " key " + key + " value " + value + " partitions " + numPartitions);
        if (value == null) {
            return 0;
        } else {

            int p = Math.abs(value.hashCode() % numPartitions);
            System.out.println("DemoPartitioner returning partition " + p);
            return p;
        }
    }
}
