package server.frames;

import java.io.*;

public class FrameInserirVoo {
    public String requestOrigem;
    public String requestDestino;
    public int requestCapacidade;
    public String replyError;

    public FrameInserirVoo(String requestOrigem, String requestDestino, int requestCapacidade) {
        this.requestOrigem = requestOrigem;
        this.requestDestino = requestDestino;
        this.requestCapacidade = requestCapacidade;
    }

    public static FrameInserirVoo deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String origem = das.readUTF();
        String destino = das.readUTF();
        int capacidade = das.readInt();
        das.close();
        bais.close();
        return new FrameInserirVoo(origem, destino, capacidade);
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
