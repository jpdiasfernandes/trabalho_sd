package client.business;

import client.business.Connection.Reply;
import client.business.Connection.Request;
import client.business.Connection.TaggedConnection;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements Runnable{
    private Socket socket;
    private ReentrantLock l = new ReentrantLock();
    private Map<Short, Reply> replies = new HashMap();
    private Map<Short, Condition> conditions = new HashMap<>();
    private TaggedConnection tagged;
    // controlador de TAG's
    // cada envio de frames será identificado com uma TAG única
    private Short currentTag = 0;

    public Demultiplexer(String server, int port){
        try {
            this.socket = new Socket(server, port);
            tagged = new TaggedConnection(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while (true){
            // read the Frame from Socket
            var reply = tagged.receive();
            // transparência
            if (reply instanceof Reply){
                replies.put(reply.getTag(),reply);
                conditions.get(reply.getTag()).signal();
            }else{
                // TODO
            }
        }
    }

    public Reply service(Request request){
        Short tagSend;
        try{
            l.lock();
            tagSend = currentTag;
            Condition c = this.l.newCondition();
            conditions.put(tagSend,c);
            tagged.send(tagSend,request);
            currentTag++;
        }finally {
            l.unlock();
        }

        Reply reply = null;
        while (reply == null){
            try {
                conditions.get(tagSend).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reply = replies.get(currentTag);
        }

        return reply;
    }
}
