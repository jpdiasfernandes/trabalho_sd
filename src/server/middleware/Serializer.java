package middleware;

import frames.ReplySerializerFrame;
import frames.SerializerFrame;

import java.io.*;
import java.net.Socket;

public class Serializer {

    private final Socket socket;

    public Serializer(Socket socket) {
        this.socket = socket;
    }

    public SerializerFrame receive() {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            Short tag = dis.readShort();
            byte opCode = dis.readByte();
            int dataSize = dis.readInt();
            byte[] data = new byte[dataSize];
            dis.readFully(data);
            //dis.close();
            return new SerializerFrame(tag, opCode, dataSize, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void send(ReplySerializerFrame reply) {
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dos.writeShort(reply.tag);
            dos.writeByte(reply.error);
            dos.writeInt(reply.size);
            if (reply.size != 0)
                dos.write(reply.data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
