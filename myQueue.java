import java.util.Iterator;
import java.util.LinkedList;

public class myQueue<T> implements Iterable<T>{
    private LinkedList<T> list;

    myQueue(){
        list = new LinkedList<>();
    }

    // Add an item to the end of the queue
    public void enqueue(T item) {
        list.addLast(item);
    }

    // Remove and return the item from the front of the queue
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        return list.removeFirst();
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }

    // Check if the queue contains a specific element
    public boolean contains(T item) {
        return list.contains(item);
    }

    public boolean delete(T item) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (current.equals(item)) {
                iterator.remove();
                return true; // Item found and deleted
            }
        }
        return false; // Item not found in the queue
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        return list.getFirst();
    }

    // use iterator to traverse the queue
    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            return list.get(index++);
        }
    }

    @Override
    public String toString() {
        return list.toString();
    }

}