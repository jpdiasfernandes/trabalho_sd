package businesslogic;

import businesslogic.excecoes.DataSemVoosException;
import businesslogic.excecoes.VooIndisponivelException;

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
    public Map<LocalDate, Map<Map.Entry<String,String>, Integer>> datasVoos;
    public Map<Integer, Viagem> viagens;

    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    public Lock rl = rwl.readLock();
    public Lock wl = rwl.writeLock();

    public VoosLN() {
        voos = new HashMap<>();
        Voo barcelonaPorto = new Voo("Barcelona", "Porto", 140);
        Voo lisboaSydney = new Voo("Lisboa", "Sydney", 450);
        Voo portoEstocolmo = new Voo("Porto", "Estocolmo", 150);
        Voo portoOslo = new Voo("Porto", "Oslo", 160);
        Voo portoKongsberg = new Voo("Porto", "Kongsberg", 135);
        Voo portoGoteborg = new Voo("Porto", "Goteborg", 160);
        Voo barcelonaLisboa = new Voo("Barcelona", "Lisboa", 120);
        Voo lisboaGoteborg = new Voo("Lisboa", "Goteborg", 135);
        Voo goteborgEstocolmo = new Voo("Goteborg", "Estocolmo", 140);

        voos.put(Map.entry(barcelonaPorto.origem, barcelonaPorto.destino), barcelonaPorto);
        voos.put(Map.entry(portoEstocolmo.origem, portoEstocolmo.destino), portoEstocolmo);
        voos.put(Map.entry(lisboaSydney.origem, lisboaSydney.destino), lisboaSydney);
        voos.put(Map.entry(portoOslo.origem, portoOslo.destino), portoOslo);
        voos.put(Map.entry(portoKongsberg.origem, portoKongsberg.destino), portoKongsberg);
        voos.put(Map.entry(portoGoteborg.origem, portoGoteborg.destino), portoGoteborg);
        voos.put(Map.entry(barcelonaLisboa.origem, barcelonaLisboa.destino), barcelonaLisboa);
        voos.put(Map.entry(lisboaGoteborg.origem, lisboaGoteborg.destino), lisboaGoteborg);
        voos.put(Map.entry(goteborgEstocolmo.origem, goteborgEstocolmo.destino), goteborgEstocolmo);

        this.datasVoos = new HashMap<>();
        this.viagens = new HashMap<>();
    }

    public Set<Viagem> getViagensData(LocalDate data) throws DataSemVoosException {
        Map<Map.Entry<String, String>, Integer> mapViagens = datasVoos.get(data);
        Set<Viagem> viagens = new HashSet<>();
        if (mapViagens == null) throw new DataSemVoosException("Esta data não tem nenhum voo");
        for (Integer codViagem : mapViagens.values()) {
            Viagem v = this.viagens.get(codViagem);
            viagens.add(v);
        }

        return viagens;
    }

    public List<String> getUsernamesData(LocalDate data) throws DataSemVoosException {
        Map<Map.Entry<String, String>, Integer> mapViagens = datasVoos.get(data);
        List<String> usernames = new ArrayList<>();
        if (mapViagens == null) throw new DataSemVoosException("Esta data não tem nenhum voo");
        for (Integer codViagem : mapViagens.values()) {
            Viagem v = this.viagens.get(codViagem);
            for (String username : v.reservas)
                usernames.add(username);
        }

        return usernames;
    }

    public void removeDia(LocalDate data) {
        Map<Map.Entry<String, String>, Integer> codViagens = this.datasVoos.get(data);
        if(codViagens != null) {
            for (Integer cod : codViagens.values())
                this.viagens.remove(cod);
            this.datasVoos.remove(data);
        }
    }

    public List<Map.Entry<String, String>> getOrigemDestino(List<String> destinos) {
        List<Map.Entry<String, String>> pairs = new ArrayList<>();
        String last = destinos.remove(0);
        int size = destinos.size();
        for (int i = 0; i < size; i++) {
            String destino = destinos.remove(0);
            pairs.add(Map.entry(last, destino));
            last = destino;
        }
        return pairs;
    }

    public List<Viagem> getViagensIntervalo(List<Map.Entry<String,String>> viagensAReservar, LocalDate dataInicial, LocalDate dataFinal)
            throws VooIndisponivelException{
        for (Map.Entry<String, String> key : viagensAReservar) {
            if (!voos.containsKey(key))
                throw new VooIndisponivelException("Pelo menos um destino não é possível acessar" +
                        " seguindo a sequência de destinos dada");
        }

        List<Viagem> r = new ArrayList<>();

        for (Map.Entry<String, String> key : viagensAReservar) {
            // a próxima reserva tem que ser na data ou após a data da ultima reserva
            Viagem v = reservarViagemIntervalo(key, dataInicial, dataFinal);
            r.add(v);
            dataInicial = v.data;
        }

        return r;
    }

    private Viagem reservarViagemIntervalo(Map.Entry<String, String> origemDestino, LocalDate dataInicial, LocalDate dataFinal)
            throws VooIndisponivelException {
        List<LocalDate> datasDisponiveis = intervaloDatas(dataInicial, dataFinal);

        for(LocalDate ld : datasDisponiveis) {
            try {
                Viagem r = getOrCreateViagem(origemDestino, ld);
                return r;
            } catch (VooIndisponivelException e) {

            }
        }

        throw new VooIndisponivelException("Não existem datas para a viagem " +
                origemDestino.getKey() + " -> " + origemDestino.getValue() + ".");
    }

    private List<LocalDate> intervaloDatas(LocalDate dataInicial, LocalDate dataFinal) {
        LocalDate ld = dataFinal.plusDays(1);
        return dataInicial.datesUntil(ld).collect(Collectors.toList());
    }

    public Viagem getOrCreateViagem(Map.Entry<String, String> origemDestino, LocalDate data) throws VooIndisponivelException {
        Integer codViagem;
        Viagem v;
        if (!voos.containsKey(origemDestino)) throw new VooIndisponivelException("Voo não existe.");
        Map<Map.Entry<String, String>, Integer> voosData = datasVoos.get(data);
        if (voosData == null) {
            datasVoos.put(data, new HashMap<>());
            voosData = datasVoos.get(data);
        }
        if ((codViagem = voosData.get(origemDestino)) == null) {
            v = new Viagem(voos.get(origemDestino), data);
            viagens.put(v.codViagem, v);
            voosData.put(origemDestino, v.codViagem);
            //Não me lembro se é preciso fazer o put outra vez, mas acho que não era preciso
            datasVoos.put(data, voosData);
        } else {
            v = viagens.get(codViagem);
        }

        if (v.reservas.size() + 1 > v.voo.capacidade) throw new VooIndisponivelException("Não existe capacidade para esta viagem");

        return v;
    }

    public Viagem getViagem(int codViagem) throws VooIndisponivelException {
        Viagem v = viagens.get(codViagem);
        if (v == null) throw new VooIndisponivelException("Este código de viagem não existe");
        return v;
    }

    public boolean voosEscala(Voo v1 , Voo v2, Voo v3, String origem, String destino) {
        return v1.origem.equals(origem)
                && v1.destino.equals(v2.origem)
                && v2.destino.equals(v3.origem)
                && v3.destino.equals(destino);
    }

    public boolean voosEscala(Voo v1, Voo v2, String origem, String destino) {
        return v1.origem.equals(origem)
                && v1.destino.equals(v2.origem)
                && v2.destino.equals(destino);
    }


}
