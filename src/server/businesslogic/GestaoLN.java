package businesslogic;

import businesslogic.excecoes.*;

import java.time.LocalDate;
import java.util.*;

public class GestaoLN {
    LockManager lm = new LockManager();
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



    public boolean validarUtilizador(String username, String pwd) throws UsernameNaoExistenteException, PasswordErradaException {
        lm.lock(this, Mode.S);
        lm.lock(contas, Mode.S);
        lm.unlock(this);
        Utilizador u = contas.contas.get(username);
        if (u != null) lm.lock(u, Mode.S);
        lm.unlock(contas);
        if (u == null) throw new UsernameNaoExistenteException("Username " + username + " não existente");
        try {
            if (!u.password.equals(pwd)) throw new PasswordErradaException("Password errada.");
        } finally {
            lm.unlock(u);
        }

        return true;
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

    public boolean admin(String username) throws UsernameNaoExistenteException {
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

        Viagem v;
        Integer codViagem;
        try {
            v = voos.getOrCreateViagem(Map.entry(origem, destino), data);
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

        v.reservas.add(username);
        lm.unlock(v);
        u.addReserva(v.codViagem);
        lm.unlock(u);

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

    public int reservarViagem(String username, LocalDate dataInicial, LocalDate dataFinal, List<String> destinos)
        throws VooIndisponivelException, UsernameNaoExistenteException {
        lm.lock(voos, Mode.X);
        lm.lock(contas, Mode.X);
        Utilizador u = contas.getConta(username);
        lm.lock(u, Mode.X);
        lm.unlock(contas);
        List<Map.Entry<String, String>> viagensAReservar = voos.getOrigemDestino(destinos);
        List<Viagem> viagens = voos.getViagensIntervalo(viagensAReservar, dataInicial, dataFinal);
        for (Viagem v : viagens) {
            lm.lock(v, Mode.X);
        }
        lm.unlock(voos);

        // Devolve o primeiro código da primeira viagem.
        // Pode sempre verificar as suas reservas
        int r = -1;
        for (Viagem v : viagens) {
            if (r == -1) r = v.codViagem;
            v.reservas.add(username);
            u.addReserva(v.codViagem);
            lm.unlock(v);
        }
        lm.unlock(u);
        return r;
    }

    public void cancelarReserva(String username, int codViagem) throws UsernameNaoExistenteException, VooIndisponivelException {
        lm.lock(voos, Mode.X);
        lm.lock(contas, Mode.X);
        Utilizador u = contas.getConta(username);
        lm.lock(u, Mode.X);
        lm.unlock(contas);
        Viagem v = voos.getViagem(codViagem);
        lm.lock(v, Mode.X);
        lm.unlock(voos);
        u.reservas.remove(codViagem);
        lm.unlock(u);
        v.reservas.remove(username);
        lm.unlock(v);
    }

    public List<Map.Entry<String, String>> getVoos() {
        List<Map.Entry<String, String>> r = new ArrayList<>();
        lm.lock(voos, Mode.S);

        for (Map.Entry<String, String> me: voos.voos.keySet())
            r.add(me);

        lm.unlock(voos);

        return r;
    }






}
