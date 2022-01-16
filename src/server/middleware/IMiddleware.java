package middleware;

import frames.ReplySerializerFrame;
import frames.SerializerFrame;

import java.util.Map;

public interface IMiddleware {
    public String putToken(String username, String pwd);
    public String getUserName(String token);
    public void submit(SerializerFrame f, int sessionID);
    public void putResponse(ReplySerializerFrame reply, int sessionID);
    public ReplySerializerFrame retrieve(int sessionID);
    public Map.Entry<Integer, SerializerFrame> bufferConsume();
    public LockManager newLockManager();
}