package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.UsernameExistente;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContasLN {
    private Map<String, Utilizador> contas;
    private Lock l = new ReentrantLock();

    public ContasLN() {
        contas = new HashMap<>();
        Utilizador u1 = new Utilizador("girafa", "1234");
        Utilizador u2 = new Utilizador("zebra", "qwerty");
        Utilizador u3 = new Admnistrador("admin", "admin123");
        contas.put(u1.username, u1);
        contas.put(u2.username, u2);
        contas.put(u3.username, u3);
    }

    public void registarUtilizador(String username, String pwd) throws UsernameExistente {
        l.lock();
        Collection<Utilizador> contasValues = contas.values();

        for(Utilizador u : contasValues)
            u.l.lock();

        l.unlock();

        try {
            if (contas.containsKey(username)) throw new UsernameExistente("Username " + username + "já existente");
            Utilizador u = new Utilizador(username, pwd);
            contas.put(username, u);
        } finally {
            for(Utilizador u : contasValues)
                u.l.unlock();
        }
    }

    public boolean validarUtilizador(String username, String pwd) {
        l.lock();
        Utilizador u = contas.get(username);
        if (u != null) u.l.lock();
        l.unlock();

        if (u == null) return false;
        else {
            try {
                return u.password.equals(pwd);
            } finally {
                u.l.unlock();
            }
        }
    }


}
