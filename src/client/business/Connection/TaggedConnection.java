package business.Connection;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaggedConnection {
    private DataOutputStream dos;
    private DataInputStream dis;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public TaggedConnection(Socket socket) {
        try {
            this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Reply receive(){
        try {
            Short tag = dis.readShort();
            byte error = dis.readByte();
            int dataSize = dis.readInt();
            byte[] data = null;
            if (dataSize != 0) {
                data = new byte[dataSize];
                dis.readFully(data);
            }
            return new Reply(tag,error,dataSize,data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void send(Short tag, Request request){
        try {
            this.dos.writeShort(tag);
            this.dos.writeByte(request.getOpcode());
            this.dos.writeInt(request.getSize());
            this.dos.write(request.getData());
            this.dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
