package business;

import business.Connection.Reply;
import business.Connection.Request;
import business.Connection.TaggedConnection;

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
            if (reply != null){
                System.out.println("Chegou uma resposta e ela é diferente de null!");
                l.lock();
                try {
                    replies.put(reply.getTag(), reply);
                    System.out.println("Vou acordar a TAG: " + reply.getTag());
                    conditions.get(reply.getTag()).signal();
                }finally {
                    l.unlock();
                }
            }else{
                // TODO
            }
        }
    }

    public Reply service(Request request){
        Reply reply = null;
        Short tagSend;
        try{
            l.lock();
            tagSend = currentTag;
            System.out.println("TAG atual: " + tagSend);
            Condition c = this.l.newCondition();
            conditions.put(tagSend,c);
            System.out.println("Enviei o request e vou adormecer até chegar o reply");
            tagged.send(tagSend,request);
            currentTag++;

            while (reply == null){
                try {
                    conditions.get(tagSend).await();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Me acordaram! Reply chegou, vou pegar a reply com tag: " + tagSend);
                reply = replies.get(tagSend);

                if (reply != null) {
                    System.out.println("... E a reply é diferente de null!");
                }else{
                    System.out.println("Fogo, afinal me acordaram e deram-me algo nulo :(");
                }
            }
        }finally {
            l.unlock();
        }

        return reply;
    }
}
