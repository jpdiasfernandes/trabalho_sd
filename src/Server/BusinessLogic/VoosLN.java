package Server.BusinessLogic;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VoosLN {
    // Voos disponiveis
    private Map<Integer, Voo> voos;
    // Voos que estão registados, se o voo não estiver aqui é porque
    // ele ainda não teve nenhuma reserva. Assim que houver a primeira
    // reserva num determinado dia, adiciona-se.
    private Map<LocalDate, Integer> datasVoos;

    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    public Lock rl = rwl.readLock();
    public Lock wl = rwl.writeLock();

    public VoosLN() {
        voos = new HashMap<>();
        Voo barcelonaPorto = new Voo("Barcelona", "Porto", 140);
        Voo lisboaSydney = new Voo("Lisboa", "Sydney", 450);
        Voo portoEstocolmo = new Voo("Porto", "Estocolmo", 150);
        Voo portoOlso = new Voo("Porto", "Oslo", 160);
        Voo portoKongsberg = new Voo("Porto", "Kongsberg", 135);
        Voo portoGoteborg = new Voo("Porto", "Goteborg", 160);
    }

    public void inserirVoo(String origem, String destino, int capacidade) {
        Voo v = new Voo(origem, destino, capacidade);
        wl.lock();
        voos.put(v.codVoo, v);
        wl.unlock();
    }

}
