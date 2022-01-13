package Server.frames;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrameReservarViagem {
    public LocalDate requestDataInicial;
    public LocalDate requestDataFinal;
    public List<String> requestDestinos;
    public int replyCodReserva;
    public String replyError;

    public void deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        this.requestDataInicial = LocalDate.parse(das.readUTF());
        this.requestDataFinal = LocalDate.parse(das.readUTF());
        int max = das.readInt();
        this.requestDestinos = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            this.requestDestinos.add(das.readUTF());
        }
        das.close();
        bais.close();
    }

    public void initializeReply(int codReserva){
        this.replyCodReserva = codReserva;
    }

    public byte[] serializeReply() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));
        dos.writeInt(replyCodReserva);
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
