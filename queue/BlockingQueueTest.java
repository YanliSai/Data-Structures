package queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTest {
    private static final int NUM = 2;
    
    //private static LinkedBlockingQueue<Integer> buffer = new LinkedBlockingQueue<>(NUM);
    private static BlockingQueue<Integer> buffer = new BlockingQueue<>(NUM);
    //private static BlockingQueue2<Integer> buffer = new BlockingQueue2<>(NUM);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM); // Create 2 threads
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
                    System.out.println("Producer puts " + (i - 1));
//                    if (buffer.size() > NUM) {
//                        System.err.println("buffer size is greater than capacity");
//                    }
                    Thread.sleep((int)(Math.random() * 10));
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
                    Thread.sleep((int)(Math.random() * 10));
                }
            } catch (InterruptedException ex) {
                
            }
        }
    }
}
