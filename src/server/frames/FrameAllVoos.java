package frames;

import java.io.*;
import java.util.List;

public class FrameAllVoos {
    public String token;
    public String requestOrigem;
    public String requestDestino;
    public List<List<String>> replyRoutes;
    public String replyError;

    public FrameAllVoos(String token, String requestOrigem, String requestDestino) {
        this.token = token;
        this.requestOrigem = requestOrigem;
        this.requestDestino = requestDestino;
    }

    public static FrameAllVoos deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        String origem = das.readUTF();
        String destino = das.readUTF();
        das.close();
        bais.close();
        return new FrameAllVoos(token,origem,destino);
    }

    public void initializeReply(List<List<String>> replyRoutes){
        this.replyRoutes = replyRoutes;
    }

    public byte[] serializeReply() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeInt(replyRoutes.size());
        for (List<String> route : replyRoutes) {
            dos.writeUTF(requestOrigem);
            for (String local : route) {
                dos.writeUTF(local);
            }
            dos.writeUTF(requestDestino);
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
