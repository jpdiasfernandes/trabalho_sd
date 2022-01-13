package Server.stub;

import Server.frames.SerializerFrame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {
    Lock l = new ReentrantLock();
    Condition notEmpty = l.newCondition();
    Condition notFull = l.newCondition();
    Queue<Object> queue = new LinkedList<>();
    public final int N = 150;

    Object get() throws InterruptedException {
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

    void put(Object obj) throws InterruptedException {
        l.lock();
        while(queue.size() >= N)
            notFull.await();
        queue.add(obj);
        notEmpty.signal();
        l.unlock();
    }

    
}
