package frames;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FramePedirVoos {
    public String token;
    public List<Map.Entry<String, String>> voos;

    public FramePedirVoos(String token) {
        this.token = token;
    }

    public static FramePedirVoos deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        das.close();
        bais.close();
        return new FramePedirVoos(token);
    }

    public void initializeReply(List<Map.Entry<String, String>> voos){
        this.voos = voos;
    }

    public byte[] serializeReply() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeInt(voos.size());
        for (Map.Entry<String,String> voo : voos) {
            dos.writeUTF(voo.getKey());
            dos.writeUTF(voo.getValue());
        }
        dos.close();
        baos.close();
        return baos.toByteArray();
    }
}
