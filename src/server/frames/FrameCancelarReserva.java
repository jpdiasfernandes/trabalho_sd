package frames;

import java.io.*;

public class FrameCancelarReserva {
    public int requestCodReserva;
    public String replyError;

    public FrameCancelarReserva(int requestCodReserva) {
        this.requestCodReserva = requestCodReserva;
    }

    public static FrameCancelarReserva deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        int codReserva = das.readInt();
        das.close();
        bais.close();
        return new FrameCancelarReserva(codReserva);
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
