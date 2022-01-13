package server.middleware;

import server.frames.ReplySerializerFrame;
import server.frames.SerializerFrame;

import java.net.Socket;

public class Serializer {

    private final Socket socket;

    public Serializer(Socket socket) {
        this.socket = socket;
    }

    public SerializerFrame receive() {
        return null;
    }

    public void send(ReplySerializerFrame reply) {

    }
}
