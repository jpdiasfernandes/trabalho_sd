package Server.BusinessLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Viagem {
    public Voo voo;
    public Set<String> reservas;
    public Lock l = new ReentrantLock();

    public Viagem(Voo voo) {
        this.voo = voo;
        this.reservas = new HashSet<>();
    }
}
