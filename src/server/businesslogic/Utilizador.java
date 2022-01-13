package server.businesslogic;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Utilizador {
    public String username;
    public String password;
    public Set<Integer> reservas;


    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
        this.reservas = new HashSet<>();
    }

    public void addReserva(Integer codViagem) {
        reservas.add(codViagem);
    }

    public void removeDiaReserva(LocalDate data) {
        reservas.remove(data);
    }
}
