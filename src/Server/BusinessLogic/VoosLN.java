package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.DataSemVoosException;
import Server.BusinessLogic.Excecoes.VooIndisponivelException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class VoosLN {
    // Voos disponiveis
    public Map<Map.Entry<String, String>, Voo> voos;
    // Voos que estão registados, se o voo não estiver aqui é porque
    // ele ainda não teve nenhuma reserva. Assim que houver a primeira
    // reserva num determinado dia, adiciona-se.
    public Map<LocalDate, Map<Map.Entry<String,String>, Viagem>> datasVoos;

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

    public Set<Viagem> getViagensData(LocalDate data) throws DataSemVoosException {
        Map<Map.Entry<String, String>, Viagem> mapViagens = datasVoos.get(data);
        Set<Viagem> viagens = new TreeSet<>();
        if (mapViagens == null) throw new DataSemVoosException("Esta data não tem nenhum voo");
        for (Viagem v : mapViagens.values())
            viagens.add(v);

        return viagens;
    }

    public Set<String> getUsernamesData(LocalDate data) throws DataSemVoosException {
        Map<Map.Entry<String, String>, Viagem> mapViagens = datasVoos.get(data);
        Set<String> usernames = new TreeSet<>();
        if (mapViagens == null) throw new DataSemVoosException("Esta data não tem nenhum voo");
        for (Viagem v : mapViagens.values()) {
            for (String username : v.reservas)
                usernames.add(username);
        }

        return usernames;
    }


}
