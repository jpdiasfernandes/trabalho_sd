package client.business;

import client.business.Exceptions.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IAirGroup11 {
    void register(String username,String password) throws RegisterInvalidException;
    String login(String username,String password) throws LoginInvalidException;
    void insertFlight(String token, String origin,String dest,Short capacity) throws InsertFlightInvalidException;
    void cancelDay(String token, LocalDateTime day) throws CancelDayInvalidException;
    String reserveTravel(String token, LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException;
    void cancelReserve(String token, String reserveCod) throws ReserveCancelException;
    List<Flight> getAllFlights(String token) throws GetFlightsException;
    List<Route> getRoutes(String token, String orig,String dest) throws GetRoutesException;
}
