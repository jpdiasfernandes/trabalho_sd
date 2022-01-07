package Server.BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Voo {
    private static final AtomicInteger count = new AtomicInteger(0);
    int codVoo;
    String origem;
    String destino;
    int capacidade;
    List<String> reservas;
    public Lock l = new ReentrantLock();

    public Voo(String origem, String destino, int capacidade) {
        this.codVoo = count.incrementAndGet();
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
        reservas = new ArrayList<>();
    }
}
