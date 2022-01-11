package Server.BusinessLogic;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    String username;
    String password;
    public Lock l = new ReentrantLock();
    private Map<LocalDate, Map.Entry<String, String>> reservas;

    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addReserva(LocalDate data, Map.Entry<String, String> vooKey) {
        reservas.put(data, vooKey);
    }

    public void removeDiaReserva(LocalDate data) {
        reservas.remove(data);
    }
}
