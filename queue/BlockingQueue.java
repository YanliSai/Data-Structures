package queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JDK version
 * Thread-safe Blocking Queue implementation using two lock queue
 * 
 * @author Jenny
 *
 */

public class BlockingQueue<E> {
    static class Node<E> {
        E item;
        Node<E> next;
        Node(E e) {
            item = e;
        }
    }
    
    private Node<E> head; // head of the linked list - points to a dummy node
    private Node<E> tail; // tail of the linked list
    
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
        head = tail = new Node<E>(null); // dummy node
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
        int count = 0; // Current count
        Node<E> node = new Node<E>(e);
        putLock.lock();
        try {
            while (this.count.get() == capacity) {
                notFull.await();
            }
            tail = tail.next = node; // enqueue
            count = this.count.incrementAndGet(); // ++i
            if (count < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        } 
        if (count == 1) { // previous count value was empty; now is not empty
            signalNotEmpty();
        }
    }
    
    public E take() throws InterruptedException {
        E e = null;
        int count = 0; // Current count
        takeLock.lock();
        try {
            while (this.count.get() == 0) {
                notEmpty.await();
            }
            e = dequeue();
            count = this.count.decrementAndGet(); // --i
            if (count > 0) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (count == capacity - 1) { // Previously was full but now not
            signalNotFull();
        }
        return e;
    }
    
    // Number of elements in the queue
    public int size() {
        return count.get();
    }
    
    // Removes a node from head of queue
    private E dequeue() {
//        E e = head.next.item;
//        head.next = head.next.next;
//        if (head.next == null) {
//            tail = head;
//        }
        head = head.next;
        E e = head.item;
        head.item = null;
        return e;
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
    
    /*
     * The returned iterator is a "weakly consistent" iterator that will never
     * throw ConcurrentModificationException, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but
     * is not guaranteed to) reflect any modifications subsequent to
     * construction.
     */
    public Iterator<E> iterator() {
        return new Itr();
    }
    
    private class Itr implements Iterator<E> {
        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;
        
        Itr() {
            fullyLock();
            try {
                current = head.next;
                if (current != null) {
                    currentElement = current.item;
                }
            } finally {
                fullyUnlock();
            }
        }
        
        @Override
        public boolean hasNext() {
            return currentElement != null;
        }

        @Override
        public E next() {
            fullyLock();
            try {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                E e = currentElement;
                lastRet = current;
                current = nextNode(current);
                currentElement = (current == null ? null : current.item);
                return e;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public void remove() {
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            fullyLock();
            try {
                Node<E> node = lastRet;
                lastRet = null;
                for (Node<E> trail = head, p =  trail.next; p != null; trail = p, p = p.next) {
                    if (p == node) {
                        // unlink p and trail
                        p.item = null;
                        trail.next = p.next;
                        if (tail == p) {
                            tail = trail;
                        }
                        if (count.getAndDecrement() == capacity) {
                            notFull.signal();
                        }
                        break;
                    }
                }
            } finally {
                fullyUnlock();
            }
        }
        
        /*
         * Returns the next live successor of current, or null if no such.
         * Unlike other traversal methods, iterators need to handle both: -
         * dequeued nodes (current.next == current) - (possibly multiple)
         * interior removed nodes (current.item == null)
         */
        private Node<E> nextNode(Node<E> current) {
            for (;;) {
                Node<E> next = current.next;
                if (next == current) {
                    return head.next;
                }
                if (next == null || next.item != null) {
                    return next;
                }
                current = next;
            }
        }
    }
    
    // Lock to prevent both puts and takes
    private void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }
    
    // Unlock to allow both puts and takes
    private void fullyUnlock() {
        putLock.lock();
        takeLock.lock();
    }
}