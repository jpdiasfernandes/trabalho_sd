package client.business;

import client.business.Connection.Reply;
import client.business.Connection.Request;
import client.business.Exceptions.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AirGroup11Stub implements IAirGroup11 {
    Demultiplexer demultiplexer = new Demultiplexer("localhost",5000);

    @Override
    public void register(String username, String password) throws RegisterInvalidException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(username.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(password.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x0,data.length,data);
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new RegisterInvalidException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }
    }

    /**
     *  @returns TOKEN
     */
    @Override
    public String login(String username, String password) throws LoginInvalidException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(username.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(password.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x1,data.length,data);
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new LoginInvalidException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }

        return new String(reply.getData(), StandardCharsets.US_ASCII);
    }

    @Override
    public void insertFlight(String token, String origin, String dest, Short capacity) throws InsertFlightInvalidException {
        Flight flight = new Flight(origin,dest,capacity);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(token.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(flight.serialize());

        Request request = new Request((byte) 0x2,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new InsertFlightInvalidException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }
    }

    @Override
    public void cancelDay(String token, LocalDateTime day) throws CancelDayInvalidException {
        String format = "MM/dd/yyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format, Locale.US);
        String dateFormatted = day.format(dateFormatter);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(token.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(dateFormatted.getBytes(StandardCharsets.UTF_8));

        Request request = new Request((byte) 0x3,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new CancelDayInvalidException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }
    }

    /**
     *  @returns RESERVE CODE
     */
    @Override
    public String reserveTravel(String token, LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException {
        String format = "MM/dd/yyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format, Locale.US);

        String startDateFormatted = start.format(dateFormatter);
        String endDateFormatted = end.format(dateFormatter);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
            dos.writeUTF(startDateFormatted);
            dos.writeUTF(endDateFormatted);

            Integer placesLength = places.size();
            dos.writeShort(placesLength.shortValue());

            for(String place: places){
                dos.writeUTF(place);
            }

            Request request = new Request((byte) 0x4,
                    baos.toByteArray().length,
                    baos.toByteArray());
            Reply reply = demultiplexer.service(request);

            if (reply.getError() == (byte) 0x0){
                throw new ReserveTravelInvalidException(new String(reply.getData(), StandardCharsets.US_ASCII));
            }

            return new String(reply.getData(),StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void cancelReserve(String token, int reserveCod) throws ReserveCancelException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
            dos.writeInt(reserveCod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request((byte) 0x5,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new ReserveCancelException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }
    }

    @Override
    public List<Flight> getAllFlights(String token) throws GetFlightsException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(token.getBytes(StandardCharsets.UTF_8));

        Request request = new Request((byte) 0x6,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new GetFlightsException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(reply.getData());
        DataInputStream dis = new DataInputStream(bais);
        try {
            int numberOfFlights = dis.readInt();

            List<Flight> flights = new ArrayList<>();
            for (int i = 0; i < numberOfFlights; i++){
                flights.add(Flight.deserialize(dis));
            }

            return flights;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Route> getRoutes(String token, String orig, String dest) throws GetRoutesException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.writeBytes(token.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(orig.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(dest.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x7,data.length,data);

        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0){
            throw new GetRoutesException(new String(reply.getData(), StandardCharsets.US_ASCII));
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(reply.getData());
        DataInputStream dis = new DataInputStream(bais);

        try {
            int numberOfPlaces = dis.readInt();

            boolean readOrigin = false;
            boolean readDest = false;

            String lastPlace = null;
            Route tmpRoute = new Route();
            List<Route> routes = new ArrayList<>();

            for (int i = 0; i < numberOfPlaces; i++){

                String tmpPlace = dis.readUTF();

                if(tmpPlace.equals(orig)) readOrigin = true;
                if(tmpPlace.equals(dest)) readDest = true;

                if (lastPlace != null && readOrigin == false){
                    Flight tmpFlight = new Flight(lastPlace,tmpPlace);
                    tmpRoute.insert(tmpFlight);
                }

                if(readDest == true) {
                    routes.add(tmpRoute.clone());
                    tmpRoute = new Route();
                }

                lastPlace = tmpPlace;
            }

            return routes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
