package middleware;

import frames.SerializerFrame;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {
    Lock l = new ReentrantLock();
    Condition notEmpty = l.newCondition();
    Condition notFull = l.newCondition();
    Queue<T> queue = new LinkedList<>();
    private final int N;

    public BoundedBuffer(int N) {
        this.N = N;
    }

    T get() throws InterruptedException {
        l.lock();
        while(queue.isEmpty())
            notEmpty.await();

        try {
            return queue.remove();
        } finally {
            notFull.signal();
            l.unlock();
        }
    }

    void put(T t) throws InterruptedException {
        l.lock();
        while(queue.size() >= N)
            notFull.await();
        queue.add(t);
        notEmpty.signal();
        l.unlock();
    }

    
}
