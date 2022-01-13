package server.frames;

import java.io.*;

public class FrameRegisto {
    public String requestUsername;
    public String requestPwd;
    public String replyError;

    public FrameRegisto() {
    }

    public void deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        this.requestUsername = das.readUTF();
        this.requestPwd = das.readUTF();
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
