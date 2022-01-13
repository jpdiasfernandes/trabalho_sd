package frames;

import java.io.*;

public class FrameLogin {
    public String token;
    public String requestUsername;
    public String requestPwd;
    public String replyToken;
    public boolean replyAdmin;

    public String replyError;

    public FrameLogin(){

    }

    public FrameLogin(String token, String requestUsername, String requestPwd) {
        this.token = token;
        this.requestUsername = requestUsername;
        this.requestPwd = requestPwd;
    }

    public static FrameLogin deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        String username = das.readUTF();
        String pwd = das.readUTF();
        das.close();
        bais.close();
        return new FrameLogin(token,username,pwd);
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
