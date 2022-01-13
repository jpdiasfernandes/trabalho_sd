package frames;

import java.io.*;
import java.time.LocalDate;

public class FrameCancelarVoos {
    public String Token;
    public LocalDate requestData;
    public String replyError;

    public FrameCancelarVoos(String token, LocalDate requestData) {
        Token = token;
        this.requestData = requestData;
    }

    public static FrameCancelarVoos deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        LocalDate localDate = LocalDate.parse(das.readUTF());
        das.close();
        bais.close();
        return new FrameCancelarVoos(token, localDate);
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
