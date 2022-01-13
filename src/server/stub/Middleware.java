package server.stub;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Middleware {
    public BoundedBuffer buffer;
    public BoundedMap map;
    public Map<String,String> tokens; //token,username
    private Lock lm = new ReentrantLock();

    //TODO adicionar controlo de concorrência
    public String putToken(String username, String pwd){
        String token = username + pwd;
        tokens.put(token, username);
        return token;
    }

    public String getUserName(String token){
        String userName = null;
        lm.lock();
        userName = tokens.get(token);
        lm.unlock();
        return userName;
    }
}
