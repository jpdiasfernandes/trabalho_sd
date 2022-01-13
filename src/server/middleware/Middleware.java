package server.middleware;

import server.frames.ReplySerializerFrame;
import server.frames.SerializerFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Middleware {
    public BoundedBuffer buffer;
    public Map<Integer, ReplySerializerFrame> responseMap;
    public Map<String,String> tokens; //token,username
    private Lock tokenL = new ReentrantLock();
    private Lock responseL = new ReentrantLock();
    private Condition responseCond = responseL.newCondition();

    public Middleware() {
        this.buffer = new BoundedBuffer();
        this.responseMap = new HashMap<>();
        this.tokens = new HashMap<>();
    }

    //TODO adicionar controlo de concorrência
    public String putToken(String username, String pwd){
        String token = username + pwd;
        tokens.put(token, username);
        return token;
    }

    public String getUserName(String token){
        String userName = null;
        tokenL.lock();
        userName = tokens.get(token);
        tokenL.unlock();
        return userName;
    }

    public void submit(SerializerFrame f, int sessionID) {
        try {
            buffer.put(f, sessionID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(ReplySerializerFrame reply, int sessionID) {
        responseL.lock();
        responseMap.put(sessionID, reply);
        responseCond.signal();
        responseL.unlock();
    }
    public ReplySerializerFrame retrieve(int sessionID) {
        responseL.lock();
        try {
            ReplySerializerFrame reply = responseMap.get(sessionID);
            while(reply == null) {
                responseCond.await();
                reply = responseMap.get(sessionID);
            }
            return reply;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            responseL.unlock();
        }
    }

    public Map.Entry<Integer, SerializerFrame> bufferConsume() {
        try {
            return buffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
