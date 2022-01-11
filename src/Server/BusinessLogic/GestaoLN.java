package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.*;
import jdk.jshell.execution.Util;

import java.time.LocalDate;
import java.util.Map;
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
        try {
            if (!voos.voos.containsKey(vooKey)) throw new VooIndisponivelException("Voo não existe.");
            Map<Map.Entry<String, String>, Viagem> voosData = voos.datasVoos.get(data);
            if ((v = voosData.get(vooKey)) == null) {
                v = new Viagem(voos.voos.get(vooKey));
                voosData.put(vooKey, v);
                //Não me lembro se é preciso fazer o put outra vez, mas acho que não era preciso
                voos.datasVoos.put(data, voosData);
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



}
