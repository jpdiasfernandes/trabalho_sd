package businesslogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utilizador {
    public String username;
    public String password;
    public List<Integer> reservas;


    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
        this.reservas = new ArrayList<>();
    }

    public void addReserva(Integer codViagem) {
        reservas.add(codViagem);
    }

    public void removeDiaReserva(LocalDate data) {
        reservas.remove(data);
    }
}
