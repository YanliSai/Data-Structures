package queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<E> {
    private Queue<E> queue = new LinkedList<E>();
    
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();  // For put
    private final Condition notEmpty = lock.newCondition(); // For take
    
    private final int capacity; // Capacity of the blocking queue
    
    public BlockingQueue() {
        capacity = Integer.MAX_VALUE;
    }
    
    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
    }
    
    public void put(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.offer(e);
            notEmpty.signal();
        } catch (InterruptedException ex) {
            
        } finally {
            lock.unlock();
        }       
    }
    
    public E take() {
        E e = null;
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            e = queue.poll();
            notFull.signal();
        } catch (InterruptedException ex) {
            
        } finally {
            lock.unlock();
        }
        return e;
    }
    
    // Number of elements in the queue
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
