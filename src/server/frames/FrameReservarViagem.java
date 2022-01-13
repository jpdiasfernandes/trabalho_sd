package frames;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrameReservarViagem {
    public String token;
    public LocalDate requestDataInicial;
    public LocalDate requestDataFinal;
    public List<String> requestDestinos;
    public int replyCodReserva;
    public String replyError;

    public FrameReservarViagem(String token, LocalDate requestDataInicial, LocalDate requestDataFinal, List<String> requestDestinos) {
        this.token = token;
        this.requestDataInicial = requestDataInicial;
        this.requestDataFinal = requestDataFinal;
        this.requestDestinos = requestDestinos;
    }

    public static FrameReservarViagem deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream das = new DataInputStream(bais);
        String token = das.readUTF();
        LocalDate dataInicial = LocalDate.parse(das.readUTF());
        LocalDate dataFinal = LocalDate.parse(das.readUTF());
        int max = das.readInt();
        List<String> destinos = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            destinos.add(das.readUTF());
        }
        das.close();
        bais.close();
        return new FrameReservarViagem(token, dataInicial, dataFinal, destinos);
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
