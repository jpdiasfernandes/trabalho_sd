package Server.frames;

import java.io.*;

public class FrameInserirVoo {
    public String requestOrigem;
    public String requestDestino;
    public int requestCapacidade;
    public String replyError;

    public void deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        this.requestOrigem = das.readUTF();
        this.requestDestino = das.readUTF();
        this.requestCapacidade = das.readInt();
        das.close();
        bais.close();
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
