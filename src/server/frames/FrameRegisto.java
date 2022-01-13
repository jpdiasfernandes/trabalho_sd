package frames;

import java.io.*;

public class FrameRegisto {
    public String requestUsername;
    public String requestPwd;
    public String replyError;

    public FrameRegisto(String requestUsername, String requestPwd) {
        this.requestUsername = requestUsername;
        this.requestPwd = requestPwd;
    }

    public static FrameRegisto deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String username = das.readUTF();
        String pwd = das.readUTF();
        das.close();
        bais.close();
        return new FrameRegisto(username, pwd);
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
