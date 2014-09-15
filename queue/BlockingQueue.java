package queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bug: take() might return null, not sure why.
 * Thread-safe Blocking Queue implementation using two lock queue
 * 
 * @author Jenny
 *
 */

public class BlockingQueue<E> {
    private Queue<E> queue = new LinkedList<E>();
    
    private final ReentrantLock putLock = new ReentrantLock();  // Gates entry to put
    private final Condition notFull = putLock.newCondition();   // waiting puts
    private final ReentrantLock takeLock = new ReentrantLock(); // Gates exit to take
    private final Condition notEmpty = takeLock.newCondition(); // waiting take
    
    // Current number of elements
    private AtomicInteger count = new AtomicInteger(0); // Avoid getting both locks
       
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
    
    /**
     * Cascading notifies are used to minimize the need for puts to get takeLocks
     * 
     * When a put notices that it has enabled at least one take,
     * it signals taker. That taker in turn signals others if more
     * items have been entered since the signal
     * 
     * @param e
     * @throws InterruptedException 
     */
    public void put(E e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        int count = -1; // Previous count
        putLock.lockInterruptibly();
        try {
            while (this.count.get() == capacity) {
                notFull.await();
            }
            queue.offer(e);
            count = this.count.getAndIncrement(); // i++
            if (count + 1 < capacity) {
                notFull.signal();
            }
        } catch (InterruptedException ex) {
            
        } finally {
            putLock.unlock();
        } 
        if (count == 0) { // previous count value was empty; now is not empty
            signalNotEmpty();
        }
    }
    
    public E take() throws InterruptedException {
        E e = null;
        int count = -1; // Previous count
        takeLock.lockInterruptibly();
        try {
            while (this.count.get() == 0) {
                notEmpty.await();
            }
            e = queue.remove();
            count = this.count.getAndDecrement(); // i--
            if (count > 1) {
                notEmpty.signal();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            takeLock.unlock();
        }
        if (count == capacity) { // Previously was full but now not
            signalNotFull();
        }
        return e;
    }
    
    // Number of elements in the queue
    public int size() {
        return count.get();
    }
    
    // Signals a waiting take. Called only from put
    private void signalNotEmpty() {
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }
    
    // Signals a waiting put. Called only from take
    private void signalNotFull() {
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }
}
