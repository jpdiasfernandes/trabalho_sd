package client;

import client.business.Connection.Reply;

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
            System.out.println("Hora de deserializar o request do cliente ...");
            DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
            Short tag = dataInputStream.readShort();
            System.out.println("Já li uma TAG!");
            byte opcode = dataInputStream.readByte();
            System.out.println("Já li um OPOCDE!");
            int dataSize = dataInputStream.readInt();
            System.out.println("Já li um DATA SIZE!");
            // etapa de ler request genérica
            byte[] data = new byte[dataSize];
            dataInputStream.readFully(data);

            // o que vem a seguir é feito caso for detetado o opcode de login
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            String username = dis.readUTF();
            System.out.println("Consegui ler o username!");
            String password = dis.readUTF();
            System.out.println("Consegui ler a password!");

            System.out.println("TAG: " + tag);
            System.out.println("OPCODE: " + opcode);
            System.out.println("DATA SIZE: " + dataSize);
            System.out.println("USERNAME: " + username);
            System.out.println("PASSWORD: " + password);

            System.out.println("Preparando a Reply...");

            String token = "tokenDoHenrique";
            String errorMessage = "Credenciais inválidas";

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            //dos.writeUTF(token);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
