package server.middleware;

import server.frames.ReplySerializerFrame;
import server.frames.SerializerFrame;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ServeState {
    public int current = 0;
    public boolean running = true;
}

public class Session {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int sessionID;
    private final Middleware mdl;
    private final Socket socket;

    public Session(Middleware mdl, Socket socket) {
        sessionID = count.getAndIncrement();
        this.mdl = mdl;
        this.socket = socket;
    }

    public void serve() {
        Serializer ser = new Serializer(this.socket);
        ServeState ss = new ServeState();
        Lock l = new ReentrantLock();
        Condition c = l.newCondition();

        // Fazer submit de pedidos
        // Submit = por a request Frame na fila de espera para os workers
        new Thread(()-> {
            l.lock();
            SerializerFrame req = ser.receive();
            if (req == null) ss.running = false;
            l.unlock();
            while(req != null) {
                l.lock();
                //TODO: verificar se este req está a devolver nulo caso a ligação feche
                ss.current++;
                mdl.submit(req, sessionID);
                c.signal();
                req = ser.receive();
                if (req == null) ss.running = false;
                l.unlock();
            }
            // O que acontece se a reply tem o lock e é preciso dizer que running é false?
        }).start();

        // Receber replies
        new Thread(()-> {
            l.lock();
            try {
                // Ainda poderá haver mais requests
                // OU já não há mais novos requests mas ainda é preciso receber
                // as replies pendentes
                while(ss.running || ss.current != 0) {
                    while(ss.current == 0 && ss.running == true)
                        c.await();

                    if (ss.current != 0 || ss.running == true) {
                        ReplySerializerFrame reply = mdl.retrieve(sessionID);
                        ser.send(reply);
                        ss.current--;
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            l.unlock();
        }).start();


    }
}
