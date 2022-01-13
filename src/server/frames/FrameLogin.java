package server.frames;

import java.io.*;

public class FrameLogin {
    public String replyToken;
    public boolean replyAdmin;
    public String requestUsername;
    public String requestPwd;
    public String replyError;

    public FrameLogin(){

    }

    public void deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        this.requestUsername = das.readUTF();
        this.requestPwd = das.readUTF();
        das.close();
        bais.close();
    }

    public void intializeRequest(String requestUsername, String requestPwd){
        this.requestUsername = requestUsername;
        this.requestPwd = requestPwd;
    }

    public void initializeReply(String replyToken, boolean replyAdmin){
        this.replyToken = replyToken;
        this.replyAdmin = replyAdmin;
    }

    public byte[] serializeReply() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeUTF(replyToken);
        dos.writeBoolean(replyAdmin);
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
