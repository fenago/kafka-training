import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

object SyncProducer extends App {

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

  try {
    val metadata: RecordMetadata = producer.send(message).get()
    println("Message is sent to Partition No. " + metadata.partition() + " and offset " + metadata.offset())
    println("Message sent successfully.")
  } catch {
    case x: Exception => {
      x.printStackTrace()
      println("Message sending failed with exception.")
    }
  }
      producer.close()
  }

