import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata, Callback}

object AsyncProducer extends App {

  val topic = args(0)
  val brokers = args(1)
  val key = "key1"
  val value = "value1"

  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "Kafka Producer")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val message = new ProducerRecord[String, String](topic, key, value)
  producer.send(message, new ProducerCallback())
  producer.close()
}
class ProducerCallback extends Callback {

  override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {

    if (e != null) {
      e.printStackTrace()
      println("Sending messages Asynchronously failed")
    }
    else println("Messages sent Asynchronously.")

  }
}