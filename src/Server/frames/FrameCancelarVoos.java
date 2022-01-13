package Server.frames;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FrameCancelarVoos {
    public LocalDate requestData;
    String replyError;

    public void deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        this.requestData = LocalDate.parse(das.readUTF());
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
