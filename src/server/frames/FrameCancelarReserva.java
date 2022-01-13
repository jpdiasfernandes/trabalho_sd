package frames;

import java.io.*;

public class FrameCancelarReserva {
    public String token;
    public int requestCodReserva;
    public String replyError;

    public FrameCancelarReserva(String token, int requestCodReserva) {
        this.token = token;
        this.requestCodReserva = requestCodReserva;
    }

    public static FrameCancelarReserva deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        int codReserva = das.readInt();
        das.close();
        bais.close();
        return new FrameCancelarReserva(token, codReserva);
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
