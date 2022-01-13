import businesslogic.GestaoLN;
import middleware.ExecuteLogic;
import middleware.Middleware;
import middleware.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        final int W = 150;
        Thread workers[] = new Thread[W];
        Middleware mdl = new Middleware();
        GestaoLN gestao = new GestaoLN();
        for (int i = 0; i < W; i++) {
            workers[i] = new Thread(new ExecuteLogic(mdl, gestao));
        }

        for (int i = 0; i < W; i++) {
            workers[i].start();
        }

        try {
            ServerSocket ss = new ServerSocket(9876);
            while(true) {
                Socket socket = ss.accept();
                new Thread(()-> {
                    Session session = new Session(mdl, socket);
                    session.serve();
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
