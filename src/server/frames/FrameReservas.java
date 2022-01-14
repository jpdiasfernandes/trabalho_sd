package frames;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FrameReservas {
    public String token;
    public List<Map.Entry<Integer,Integer>> replyReservas;
    public String replyError;

    public FrameReservas(String token) {
        this.token = token;
    }

    public static FrameReservas deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        das.close();
        bais.close();
        return new FrameReservas(token);
    }

    public void initializeReply(List<Map.Entry<Integer, Integer>> replyReservas){
        this.replyReservas = replyReservas;
    }

    public byte[] serializeReply() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeInt(replyReservas.size());
        for (Map.Entry<Integer,Integer> reserva : replyReservas) {
            dos.writeInt(reserva.getKey());
            dos.writeInt(reserva.getValue());
        }
        dos.close();
        baos.close();
        return baos.toByteArray();
    }

    public void initializeError(String replyError){
        this.replyError = replyError;
    }

    public byte[] serializeError() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeUTF(replyError);
        dos.close();
        baos.close();
        return baos.toByteArray();
    }
}
