package queue;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue2<E> {
    Queue<E> queue = new LinkedList<E>();
    private final int capacity; // Capacity of the blocking queue
    
    public BlockingQueue2() {
        capacity = Integer.MAX_VALUE;
    }
    
    public BlockingQueue2(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
    }
    
    public synchronized void put(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        try {
            while (queue.size() == capacity) {
                wait();
            }
            queue.offer(e); 
            if (queue.size() == 1) { // Notify only if the queue previously was empty
                notifyAll();
            }                      
        } catch (InterruptedException ex) {
            
        }
    }
    
    public synchronized E take() {
        E e = null;
        try {
            while (queue.isEmpty()) {
                wait();
            }
            e = queue.poll();
            if (queue.size() == capacity - 1) { // Notify only if the queue was full
                notifyAll();
            }
        } catch (InterruptedException ex) {
            
        }
        return e;
    }
    
    public synchronized int size() {
        return queue.size();
    }
}
