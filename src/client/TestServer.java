package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(9876);
            System.out.println("Server listen in port 9876 ...");
            while(true){
                Socket clientSocket = ss.accept();
                TestServerWorker ts = new TestServerWorker(clientSocket);
                Thread t = new Thread(ts);
                System.out.println("Chegou um cliente, vou atendê-lo ...");
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
