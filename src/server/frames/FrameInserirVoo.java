package frames;

import java.io.*;

public class FrameInserirVoo {
    public String token;
    public String requestOrigem;
    public String requestDestino;
    public short requestCapacidade;
    public String replyError;

    public FrameInserirVoo(String token, String requestOrigem, String requestDestino, short requestCapacidade) {
        this.token = token;
        this.requestOrigem = requestOrigem;
        this.requestDestino = requestDestino;
        this.requestCapacidade = requestCapacidade;
    }

    public static FrameInserirVoo deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        String origem = das.readUTF();
        String destino = das.readUTF();
        short capacidade = das.readShort();
        das.close();
        bais.close();
        return new FrameInserirVoo(token, origem, destino, capacidade);
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
