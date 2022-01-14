package business;

import business.Exceptions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IAirGroup11 {
    void register(String username,String password) throws RegisterInvalidException;
    Map.Entry<String,Boolean> login(String username, String password) throws LoginInvalidException;
    void insertFlight(String token, String origin,String dest,Short capacity) throws InsertFlightInvalidException;
    void cancelDay(String token, LocalDateTime day) throws CancelDayInvalidException;
    int reserveTravel(String token, LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException;
    void cancelReserve(String token, int reserveCod) throws ReserveCancelException;
    List<Flight> getAllFlights(String token) throws GetFlightsException;
    List<Route> getRoutes(String token, String orig,String dest) throws GetRoutesException;
    List<Map.Entry<Integer,Integer>> getMyReserves(String token) throws GetMyReservesException;
}
