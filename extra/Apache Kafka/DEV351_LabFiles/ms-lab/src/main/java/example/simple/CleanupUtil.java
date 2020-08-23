package example.simple;

import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * 
 */
public class CleanupUtil extends Thread {
   Thread t;
    public CleanupUtil(Thread t){
        this.t = t;
    }

    @Override
    public void run()  {
        try {
            System.out.println("Inside CleanupUtil.run " + Thread.currentThread().getName());
            System.out.println("Interrupting " + t.isAlive() + t.getState());
            t.interrupt();
            t.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
