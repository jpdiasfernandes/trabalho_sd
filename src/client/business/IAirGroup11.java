package client.business;

import client.business.Exceptions.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IAirGroup11 {
    void register(String username,String password) throws RegisterInvalidException;
    String login(String username,String password) throws LoginInvalidException;
    void insertFlight(String origin,String dest,Short capacity) throws InsertFlightInvalidException;
    void cancelDay(LocalDateTime day) throws CancelDayInvalidException;
    String reserveTravel(LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException;
    void cancelReserve(String reserveCod) throws ReserveCancelException;
    List<Flight> getAllFlights() throws GetFlightsException;
    List<Route> getRoutes(String orig,String dest) throws GetRoutesException;
}
