package frames;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FramePedirVoos {
    public List<Map.Entry<String, String>> voos;

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
