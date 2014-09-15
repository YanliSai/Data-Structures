package queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTest {
    private static LinkedBlockingQueue<Integer> buffer = new LinkedBlockingQueue<>(2);
    //private static BlockingQueue<Integer> buffer = new BlockingQueue<>(2);
    //private static BlockingQueue2<Integer> buffer = new BlockingQueue2<>(2);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2); // Create 2 threads
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());
        executor.shutdown();
    }

    // Adding an int to the buffer
    private static class ProducerTask implements Runnable {
        public void run() {
            try {
                int i = 1;
                while (true) {                    
                    buffer.put(i++);
                    System.out.println("Producer puts " + i);
                    Thread.sleep((int)(Math.random() * 1000));
                }
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    // Reading and deleting an int from the buffer
    private static class ConsumerTask implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\t\tConsumer takes " + buffer.take());
                    Thread.sleep((int)(Math.random() * 1000));
                }
            } catch (InterruptedException ex) {
                
            }
        }
    }
}
