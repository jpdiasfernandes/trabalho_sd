package client.business.Connection;

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

    public Frame receive(){
        try {
            Short tag = dis.readShort();
            int frameSize = dis.readInt();
            byte[] frameData = new byte[frameSize];
            dis.readFully(frameData);
            return new Frame(tag,frameSize,frameData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void send(Short tag, Frame frame){
        //lock.writeLock().lock();
      //  try{
            try {
                this.dos.writeShort(tag);
                this.dos.writeInt(frame.getSize());
                this.dos.write(frame.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }finally {
       //     lock.writeLock().unlock();
       // }
    }
}
