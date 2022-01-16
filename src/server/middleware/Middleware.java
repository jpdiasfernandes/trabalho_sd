package middleware;

import frames.ReplySerializerFrame;
import frames.SerializerFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Middleware implements IMiddleware {
    public BoundedBuffer<Map.Entry<Integer, SerializerFrame>> workersBuffer;
    public HashMap<Integer, BoundedBuffer<ReplySerializerFrame>> responseMap;
    public HashMap<String,String> tokens; //token,username
    private Lock tokenL = new ReentrantLock();
    private Lock responseL = new ReentrantLock();
    private final int workersBufferN = 150;
    private final int replyBufferN = 5;

    public Middleware() {
        this.workersBuffer = new BoundedBuffer<>(workersBufferN);
        this.responseMap = new HashMap<>();
        this.tokens = new HashMap<>();
    }

    //TODO adicionar controlo de concorrência
    public String putToken(String username, String pwd){
        String token = username + pwd;
        tokenL.lock();
        tokens.put(token, username);
        tokenL.unlock();
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
            responseL.lock();
            BoundedBuffer<ReplySerializerFrame> b = responseMap.get(sessionID);
            if (b == null) {
                responseMap.put(sessionID, new BoundedBuffer<>(replyBufferN));
            }
            responseL.unlock();

            workersBuffer.put(Map.entry(sessionID, f));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(ReplySerializerFrame reply, int sessionID) {
        responseL.lock();
        BoundedBuffer<ReplySerializerFrame> b = responseMap.get(sessionID);
        try {
            b.put(reply);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responseL.unlock();
    }
    public ReplySerializerFrame retrieve(int sessionID) {
        responseL.lock();
        BoundedBuffer<ReplySerializerFrame> b = responseMap.get(sessionID);
        responseL.unlock();

        try {
            ReplySerializerFrame reply = b.get();
            return reply;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map.Entry<Integer, SerializerFrame> bufferConsume() {
        try {
            return workersBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LockManager newLockManager() {
        return new LockManager();
    }
}
