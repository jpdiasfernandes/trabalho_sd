package client.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Route {
    private List<Flight> route;

    public Route() {
        this.route = new ArrayList<>();
    }

    public void insert(Flight f){
        route.add(f);
    }

    public List<Flight> getRoute(){
        return route.stream().map(Flight::clone).collect(Collectors.toList());
    }
}
