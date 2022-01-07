package client.business;

import client.business.Connection.Frame;
import client.business.Exceptions.*;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class AirGroup11Stub implements IAirGroup11 {
    Demultiplexer demultiplexer = new Demultiplexer("localhost",5000);

    @Override
    public void register(String username, String password) throws RegisterInvalidException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(username.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(password.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();
        int size = data.length;

        Frame request = new Frame(size,data);
        Frame reply = demultiplexer.service(request);
        // deserializar o array de bytes de acordo com a mensagem de resposta do register
        reply.getData();
    }

    @Override
    public String login(String username, String password) throws LoginInvalidException {
        return null;
    }

    @Override
    public void insertFlight(String origin, String dest, Short capacity) throws InsertVooInvalidException {

    }

    @Override
    public void cancelDay(LocalDateTime day) throws CancelDayInvalidException {

    }

    @Override
    public String reserveTravel(LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException {
        return null;
    }

    @Override
    public void cancelReserve(String reserveCod) throws ReserveCancelException {

    }

    @Override
    public List<Flight> getAllFlights() throws GetFlightsException {
        return null;
    }

    @Override
    public List<Route> getRoutes(String orig, String dest) throws GetRoutesException {
        return null;
    }
}
