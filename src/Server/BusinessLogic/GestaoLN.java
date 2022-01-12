package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.*;
import jdk.jshell.execution.Util;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GestaoLN {
    LockManager lm = new LockManager(this);
    public ContasLN contas = new ContasLN();
    public VoosLN voos = new VoosLN();

    public void registarUtilizador(String username, String pwd) throws UsernameExistenteException {
        lm.lock(this, Mode.X);
        lm.lock(contas, Mode.X);
        lm.unlock(this);
        try {
           if(contas.contas.containsKey(username)) throw new UsernameExistenteException("Username " + username + " já existente");
           Utilizador u = new Utilizador(username, pwd);
           contas.contas.put(username, u);
        } finally {
            lm.unlock(contas);
        }
    }



    public boolean validarUtilizador(String username, String pwd) throws UsernameNaoExistenteException {
        lm.lock(this, Mode.S);
        lm.lock(contas, Mode.S);
        lm.unlock(this);
        Utilizador u = contas.contas.get(username);
        if (u != null) lm.lock(u, Mode.S);
        lm.unlock(contas);
        if (u == null) throw new UsernameNaoExistenteException("Username " + username + " não existente");
        try {
            return u.password.equals(pwd);
        } finally {
            lm.unlock(u);
        }
    }

    public void insercaoVoo(String username, String origem, String destino, int capacidade)
            throws NaoTemPermissaoException, UsernameNaoExistenteException {
        lm.lock(this, Mode.X);
        if (!admin(username)) throw new NaoTemPermissaoException("Username " + username + " não tem permissão para" +
                " inserir um voo");
        lm.lock(voos, Mode.X);
        lm.unlock(this);
        Voo v = new Voo(origem, destino, capacidade);
        voos.voos.put(Map.entry(origem, destino), v);
        lm.unlock(voos);
    }

    private boolean admin(String username) throws UsernameNaoExistenteException {
        lm.lock(contas, Mode.S);
        Utilizador u = contas.contas.get(username);
        if (u != null) lm.lock(u, Mode.S);
        lm.unlock(contas);

        if (u == null) throw new UsernameNaoExistenteException("Username " + username + " não existente.");
        try {
            return u instanceof Admnistrador;
        } finally {
            lm.unlock(u);
        }
    }

    public void reservarVoo(String username, LocalDate data, String origem, String destino)
            throws UsernameNaoExistenteException, VooIndisponivelException {
        lm.lock(this, Mode.X);
        lm.lock(voos, Mode.X);
        lm.lock(contas, Mode.X);
        lm.unlock(this);

        Map.Entry<String, String> vooKey = Map.entry(origem, destino);
        Viagem v;
        Integer codViagem;
        try {
            if (!voos.voos.containsKey(vooKey)) throw new VooIndisponivelException("Voo não existe.");
            Map<Map.Entry<String, String>, Integer> voosData = voos.datasVoos.get(data);
            if ((codViagem = voosData.get(vooKey)) == null) {
                v = new Viagem(voos.voos.get(vooKey), data);
                voos.viagens.put(codViagem, v);
                voosData.put(vooKey, v.codViagem);
                //Não me lembro se é preciso fazer o put outra vez, mas acho que não era preciso
                voos.datasVoos.put(data, voosData);
            } else {
                v = voos.viagens.get(codViagem);
            }



            lm.lock(v, Mode.X);
        } finally {
            lm.unlock(voos);
        }

        Utilizador u;
        try {
            u = contas.getConta(username);
            lm.lock(u, Mode.X);
        } finally {
            lm.unlock(contas);
        }


        try {
            if (v.reservas.size() + 1 > v.voo.capacidade) throw new VooIndisponivelException("Não existem vagas para o voo.");
            v.reservas.add(username);
            u.addReserva(data, vooKey);
        } finally {
            lm.unlock(v);
            lm.unlock(u);
        }

    }
public void cancelarDia(LocalDate data) throws DataSemVoosException {
        // No fim fazer refactor para tirar este lock do this
        // Se se fizer um lock ordenado sempre lock voos e depois contas não é preciso
    lm.lock(this, Mode.X);
    lm.lock(voos, Mode.X);
    lm.lock(this.contas, Mode.X);
    lm.unlock(this);
    Set<String> usernames = voos.getUsernamesData(data);
    voos.removeDia(data);
    lm.unlock(voos);
    Set<Utilizador> contas = new TreeSet<>();

    // Ainda consigo otimizar isto
    try {
        for(String username : usernames) {
            Utilizador u = this.contas.getConta(username);
            contas.add(u);
        }
        contas.stream().sorted().forEach(u-> lm.lock(u, Mode.X));
    } catch (UsernameNaoExistenteException e) {
        e.printStackTrace();
    }
    lm.unlock(this.contas);
    // Se calhar depois pode-se criar notificações
    for (Utilizador u : contas) {
        u.removeDiaReserva(data);
        lm.unlock(u);
    }

}



}
