package businesslogic;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Viagem {
    private static final AtomicInteger count = new AtomicInteger(0);
    public final int codViagem;
    public Voo voo;
    public Set<String> reservas;
    public LocalDate data;

    public Viagem(Voo voo, LocalDate data) {
        this.voo = voo;
        this.reservas = new HashSet<>();
        this.data = data;
        this.codViagem = count.incrementAndGet();
    }
}
