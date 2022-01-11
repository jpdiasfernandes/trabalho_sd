package Server.BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Voo {
    public final String origem;
    public final String destino;
    public final int capacidade;

    public Voo(String origem, String destino, int capacidade) {
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
    }
}
