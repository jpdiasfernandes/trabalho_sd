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

    public void removeReserva(int codViagem) {
        int size = reservas.size();
        for(int i = 0; i < size; i++) {
            if (reservas.get(i) == codViagem)
                reservas.remove(i);
        }
    }
}
