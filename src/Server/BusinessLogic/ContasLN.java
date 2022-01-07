package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.UsernameExistenteException;
import Server.BusinessLogic.Excecoes.UsernameNaoExistenteException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ContasLN {
    private Map<String, Utilizador> contas;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock rl = rwl.readLock();
    private Lock wl = rwl.writeLock();

    public ContasLN() {
        contas = new HashMap<>();
        Utilizador u1 = new Utilizador("girafa", "1234");
        Utilizador u2 = new Utilizador("zebra", "qwerty");
        Utilizador u3 = new Admnistrador("admin", "admin123");
        contas.put(u1.username, u1);
        contas.put(u2.username, u2);
        contas.put(u3.username, u3);
    }

    public void registarUtilizador(String username, String pwd) throws UsernameExistenteException {
        wl.lock();
        Collection<Utilizador> contasValues = contas.values();

        try {
            if (contas.containsKey(username)) throw new UsernameExistenteException("Username " + username + "já existente");
            Utilizador u = new Utilizador(username, pwd);
            contas.put(username, u);
        } finally {
            wl.unlock();
        }
    }

    public boolean validarUtilizador(String username, String pwd) throws UsernameNaoExistenteException {
        rl.lock();
        Utilizador u = contas.get(username);
        if (u != null) u.l.lock();
        rl.unlock();

        if (u == null) throw new UsernameNaoExistenteException();
        else {
            try {
                return u.password.equals(pwd);
            } finally {
                u.l.unlock();
            }
        }
    }

    public boolean admin(String username) throws UsernameNaoExistenteException {
        rl.lock();
        Utilizador u = contas.get(username);
        if (u != null) u.l.lock();
        rl.unlock();

        if (u == null) throw new UsernameNaoExistenteException();
        else {
            try {
                return u instanceof Admnistrador;
            } finally {
                u.l.unlock();
            }
        }

    }


}
