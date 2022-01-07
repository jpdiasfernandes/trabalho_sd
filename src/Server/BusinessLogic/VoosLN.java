package Server.BusinessLogic;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VoosLN {
    // Voos disponiveis
    private Map<Integer, Voo> voos;
    // Voos que estão registados, se o voo não estiver aqui é porque
    // ele ainda não teve nenhuma reserva. Assim que houver a primeira
    // reserva num determinado dia, adiciona-se.
    private Map<LocalDate, Integer> datasVoos;

    public VoosLN() {
        voos = new HashMap<>();
        Voo barcelonaPorto = new Voo("Barcelona", "Porto", 140);
        Voo lisboaSydney = new Voo("Lisboa", "Sydney", 450);
        Voo portoEstocolmo = new Voo("Porto", "Estocolmo", 150);
        Voo portoOlso = new Voo("Porto", "Oslo", 160);
        Voo portoKongsberg = new Voo("Porto", "Kongsberg", 135);
        Voo portoGoteborg = new Voo("Porto", "Goteborg", 160);
    }

}
