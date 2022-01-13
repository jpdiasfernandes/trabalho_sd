import business.Connection.Reply;
import ui.Colors;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public
class TestServerWorker implements Runnable{
    private Socket s;

    public TestServerWorker(Socket s){
        this.s = s;
    }

    @Override
    public void run() {
        try {
            while(true) {
                System.out.println("Hora de deserializar o request do cliente ...");
                DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
                Short tag = dataInputStream.readShort();
                System.out.print("Já li uma TAG! ->");
                byte opcode = dataInputStream.readByte();
                System.out.print("Já li um OPOCDE! -> ");
                int dataSize = dataInputStream.readInt();
                System.out.print("Já li um DATA SIZE! \n");
                // etapa de ler request genérica
                byte[] data = new byte[dataSize];
                dataInputStream.readFully(data);

                System.out.println("TAG: " + tag);
                System.out.println("OPCODE: " + opcode);
                System.out.println("DATA SIZE: " + dataSize);

                if (opcode == (byte)0x0) answerRegister(data,tag);
                if (opcode == (byte)0x1) answerLogin(data,tag);
                if (opcode == (byte)0x4) answerReserve(data,tag);
                if (opcode == (byte)0x5) answerCancelReserve(data,tag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerLogin(byte[] data,Short tag) throws IOException {
        System.out.println(Colors.ANSI_GREEN + "Foi detetado um Request de Login!" + Colors.ANSI_RESET);
        // o que vem a seguir é feito caso for detetado o opcode de login
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        String username = dis.readUTF();
        System.out.println("Consegui ler o username!");
        String password = dis.readUTF();
        System.out.println("Consegui ler a password!");

        System.out.println("USERNAME: " + username);
        System.out.println("PASSWORD: " + password);

        System.out.println("Preparando a Reply...");

        String token = "tokenDoHenrique";
        String errorMessage = "Credenciais inválidas";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(token);
        // isAdmin
        dos.writeBoolean(true);
        //dos.writeUTF(errorMessage);

        Reply r = new Reply(
                tag,
                (byte) 0x1,
                baos.toByteArray().length,
                baos.toByteArray()
        );

        DataOutputStream dos2 = new DataOutputStream(s.getOutputStream());
        System.out.println("Enviando a Reply...");
        dos2.write(r.deserialize());
        dos2.flush();
        System.out.println("Reply foi enviada");
    }
    private void answerRegister(byte[] data,Short tag) throws IOException {
        System.out.println(Colors.ANSI_GREEN + "Foi detetado um Request de Register !" + Colors.ANSI_RESET);
        // o que vem a seguir é feito caso for detetado o opcode de registo
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        String username = dis.readUTF();
        System.out.println("Consegui ler o username!");
        String password = dis.readUTF();
        System.out.println("Consegui ler a password!");

        System.out.println("USERNAME: " + username);
        System.out.println("PASSWORD: " + password);

        System.out.println("Preparando a Reply...");

        String errorMessage = "Registo inválido";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(errorMessage);

        Reply r = new Reply(
                tag,
                (byte) 0x0,
                baos.toByteArray().length,
                baos.toByteArray()
        );

        DataOutputStream dos2 = new DataOutputStream(s.getOutputStream());
        System.out.println("Enviando a Reply...");
        dos2.write(r.deserialize());
        dos2.flush();
        System.out.println("Reply foi enviada");
    }
    private void answerReserve(byte[] data,Short tag) throws IOException{
        System.out.println(Colors.ANSI_GREEN + "Foi detetado um Request de Reserve!" + Colors.ANSI_RESET);
        // o que vem a seguir é feito caso for detetado o opcode de reserve
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        String token = dis.readUTF();
        System.out.println("Consegui ler o token!");
        String startDay = dis.readUTF();
        System.out.println("Consegui ler o dia inicial!");
        String endDay = dis.readUTF();
        System.out.println("Consegui ler o dia final!");

        System.out.println("TOKEN: " + token);
        System.out.println("START DAY: " + startDay);
        System.out.println("END DAY: " + endDay);

        System.out.println("Preparando a Reply...");

        int reserveCode = 1235;
        String errorMessage = "token inválido";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        //dos.writeInt(reserveCode);
        dos.writeUTF(errorMessage);

        Reply r = new Reply(
                tag,
                (byte) 0x0,
                baos.toByteArray().length,
                baos.toByteArray()
        );

        DataOutputStream dos2 = new DataOutputStream(s.getOutputStream());
        System.out.println("Enviando a Reply...");
        dos2.write(r.deserialize());
        dos2.flush();
        System.out.println("Reply foi enviada");
    }
    private void answerCancelReserve(byte[] data,Short tag) throws IOException {
        System.out.println(Colors.ANSI_GREEN + "Foi detetado um Request de Cancel Reserve!" + Colors.ANSI_RESET);
        // o que vem a seguir é feito caso for detetado o opcode de cancel reserve
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        String token = dis.readUTF();
        System.out.println("Consegui ler o token!");
        int reserveCode = dis.readInt();
        System.out.println("Consegui ler o código da reserva!");

        System.out.println("TOKEN: " + token);
        System.out.println("RESERVECODE: " + reserveCode);

        System.out.println("Preparando a Reply...");

        String errorMessage = "Credenciais inválidas";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(errorMessage);

        Reply r = new Reply(
                tag,
                (byte) 0x1,
                0,
                null
        );

        DataOutputStream dos2 = new DataOutputStream(s.getOutputStream());
        System.out.println("Enviando a Reply...");
        dos2.write(r.deserialize());
        dos2.flush();
        System.out.println("Reply foi enviada");
    }
}
