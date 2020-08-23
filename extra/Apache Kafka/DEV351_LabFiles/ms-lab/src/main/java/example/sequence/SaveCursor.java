package example.sequence;

import java.util.Collection;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

public class SaveCursor implements ConsumerRebalanceListener {

    private Consumer<?, ?> consumer;

    public SaveCursor(Consumer<?, ?> consumer) {
        this.consumer = consumer;
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        System.out.print("Parititions assigned:");
        for (TopicPartition p : partitions) {
            System.out.println(" " + p.topic() + "(" + p.partition() + ")");
        }
        System.out.println();
    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        this.consumer.commitSync();
        System.out.print("partitions revoked:");
        for (TopicPartition p : partitions) {
            System.out.println(" " + p.topic() + "(" + p.partition() + ")");
        }
        System.out.println();
    }
}
