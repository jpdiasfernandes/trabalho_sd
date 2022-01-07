package Server.BusinessLogic;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    String username;
    String password;
    public Lock l = new ReentrantLock();

    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
