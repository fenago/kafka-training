from confluent_kafka import Consumer, KafkaError

c = Consumer({'bootstrap.servers': 'localhost:9092,localhost:9093,localhost:9094',
            'group.id': 'myPyConsumer'
              })
c.subscribe(['stock-prices'])


running = True
while running:
    msg = c.poll()
    if not msg.error():
        print('Received message: %s' % msg.value().decode('utf-8'))
    elif msg.error().code() != KafkaError._PARTITION_EOF:
        print(msg.error())
        running = False
c.close()