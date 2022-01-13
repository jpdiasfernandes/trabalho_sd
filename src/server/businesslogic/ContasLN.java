package server.businesslogic;

import server.businesslogic.excecoes.UsernameNaoExistenteException;

import java.util.HashMap;
import java.util.Map;

public class ContasLN {
    public Map<String, Utilizador> contas;
    public ContasLN() {
        contas = new HashMap<>();
        Utilizador u1 = new Utilizador("girafa", "1234");
        Utilizador u2 = new Utilizador("zebra", "qwerty");
        Utilizador u3 = new Admnistrador("admin", "admin123");
        contas.put(u1.username, u1);
        contas.put(u2.username, u2);
        contas.put(u3.username, u3);
    }

    public Utilizador getConta(String username) throws UsernameNaoExistenteException {
        Utilizador u  = contas.get(username);
        if (u == null) throw new UsernameNaoExistenteException("Username não existente.");
        return u;
    }



}
