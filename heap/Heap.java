package heap;

import java.util.ArrayList;
import java.util.Collections;

// Min Heap
public class Heap<E extends Comparable<E>> {
    ArrayList<E> list = new ArrayList<>();
    
    public Heap() {}

     // O(N) - starting from construct an array (list) with arbitrary order.
     // Then, move down beginning from the deepest non-leaf node  (2N - 2 - logN)
    public Heap(E[] objects) {
        Collections.addAll(list, objects);
        for (int i = list.size() / 2 - 1; i >= 0; i--) { //!!! double check the range
            moveDown(i);
        }
    }
    
    // Add a new element into the heap - O(logN)
    public void add(E newObj) {
        list.add(newObj);
        moveUp();
    }
    
    // Remove the root from the heap - O(logN)
    public E remove() {
        if (list.isEmpty()) {
            return null;
        }
        
        E removeObj = list.get(0);
        list.set(0, list.get(list.size() - 1)); // New root: need move down
        list.remove(list.size() - 1); // Remove the last element
        
        moveDown(0);
        return removeObj;
    }

    private void moveUp() {
        int currentIdx = list.size() - 1;
        while (currentIdx > 0) {
            int parentIdx = (currentIdx - 1) / 2;
            // Swap if current obj < parent obj
            if (list.get(currentIdx).compareTo(list.get(parentIdx)) < 0) {
                Collections.swap(list, currentIdx, parentIdx);
                currentIdx = parentIdx;
            } else { // !!! No need move up if current obj is already small
                break;
            }            
        }
    }
    
    private void moveDown(int currentIdx) {
        while (currentIdx < list.size() / 2) {
            int leftChildIdx = 2 * currentIdx + 1;
            int rightChildIdx = 2 * currentIdx + 2;
            
            if (leftChildIdx >= list.size()) { // already a heap
                break;
            }
            int minIdx = leftChildIdx;
            if (rightChildIdx < list.size() &&
                    list.get(rightChildIdx).compareTo(list.get(minIdx)) < 0) {
                minIdx = rightChildIdx;
            }
            
            // Swap if obj[minIdx] < current obj
            if (list.get(minIdx).compareTo(list.get(currentIdx)) < 0) {
                Collections.swap(list,  minIdx, currentIdx);
                currentIdx = minIdx;
            } else { // Already a heap
                break;
            }            
        }
    }
}
