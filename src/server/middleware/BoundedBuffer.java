package server.middleware;

import server.frames.SerializerFrame;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {
    Lock l = new ReentrantLock();
    Condition notEmpty = l.newCondition();
    Condition notFull = l.newCondition();
    Queue<Map.Entry<Integer, SerializerFrame>> queue = new LinkedList<>();
    public final int N = 150;

    Map.Entry<Integer, SerializerFrame> get() throws InterruptedException {
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

    void put(SerializerFrame obj, int id) throws InterruptedException {
        l.lock();
        while(queue.size() >= N)
            notFull.await();
        queue.add(Map.entry(id, obj));
        notEmpty.signal();
        l.unlock();
    }

    
}
