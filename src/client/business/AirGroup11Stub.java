package business;

import connection.Demultiplexer;
import connection.Reply;
import connection.Request;
import business.Exceptions.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AirGroup11Stub implements IAirGroup11 {
    Demultiplexer demultiplexer;

    public AirGroup11Stub(){
        demultiplexer = new Demultiplexer("localhost",9876);
        Thread thread = new Thread(demultiplexer);
        thread.start();
    }

    @Override
    public void register(String username, String password) throws RegisterInvalidException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(username);
            dos.writeUTF(password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x0,data.length,data);
        Reply reply = demultiplexer.service(request);

        if (reply.getError() == (byte) 0x0 && reply.getDataSize() > 0){
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RegisterInvalidException(message);
        }
    }

    /**
     *  @returns <TOKEN,IsADMIN>
     */
    @Override
    public Map.Entry<String,Boolean> login(String username, String password) throws LoginInvalidException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(username);
            dos.writeUTF(password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x1,data.length,data);
        Reply reply = demultiplexer.service(request);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));

        if (reply.getError() == (byte) 0x0 && reply.getDataSize() > 0){
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new LoginInvalidException(message);
        }

        String token = null;
        boolean isAdmin = false;
        Map.Entry<String,Boolean> loginAnswer = null;
        try {
            if (reply.getDataSize() > 0)
                token = dis.readUTF();
                isAdmin = dis.readBoolean();
                loginAnswer = Map.entry(token,isAdmin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loginAnswer;
    }

    @Override
    public void insertFlight(String token, String origin, String dest, Short capacity) throws InsertFlightInvalidException {
        Flight flight = new Flight(origin,dest,capacity);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
            dos.write(flight.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request((byte) 0x2,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);


        if (reply.getError() == (byte) 0x0 && reply.getDataSize() > 0){
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new InsertFlightInvalidException(message);
        }
    }

    @Override
    public void cancelDay(String token, LocalDateTime day) throws CancelDayInvalidException {
        String format = "MM/dd/yyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format, Locale.US);
        String dateFormatted = day.format(dateFormatter);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
            dos.writeUTF(dateFormatted);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request((byte) 0x3,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);


        if (reply.getError() == (byte) 0x0 && reply.getDataSize() > 0){
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new CancelDayInvalidException(message);
        }
    }

    /**
     *  @returns FIRST TRAVEL RESERVE CODE
     */
    @Override
    public int reserveTravel(String token, LocalDateTime start, LocalDateTime end, List<String> places) throws ReserveTravelInvalidException {
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
                //System.out.println(place);
                dos.writeUTF(place);
            }

            Request request = new Request((byte) 0x4,
                    baos.toByteArray().length,
                    baos.toByteArray());
            Reply reply = demultiplexer.service(request);

            DataInputStream dis = null;
            if (reply.getDataSize() > 0) {
                dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));
            }

            if (reply.getError() == (byte) 0x0){
                String message = null;
                try {
                    message = dis.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new ReserveTravelInvalidException(message);
            }

            int reserveCode = -1;
            try {
                reserveCode = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reserveCode;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
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

        if (reply.getError() == (byte) 0x0 && reply.getDataSize() > 0){
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(reply.getData()));
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new ReserveCancelException(message);
        }
    }

    @Override
    public List<Flight> getAllFlights(String token) throws GetFlightsException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request((byte) 0x6,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        ByteArrayInputStream bais = new ByteArrayInputStream(reply.getData());
        DataInputStream dis = new DataInputStream(bais);

        if (reply.getError() == (byte) 0x0){
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new GetFlightsException(message);
        }

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
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
            dos.writeUTF(orig);
            dos.writeUTF(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = baos.toByteArray();

        Request request = new Request((byte) 0x7,data.length,data);
        Reply reply = demultiplexer.service(request);

        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        if (reply.getDataSize() > 0) {
            bais = new ByteArrayInputStream(reply.getData());
            dis = new DataInputStream(bais);
        }

        if (reply.getError() == (byte) 0x0){
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new GetRoutesException(new String(message));
        }

        try {
            int numberOfPlaces = dis.readInt();

            boolean readOrigin = false;
            boolean readDest = false;

            String lastPlace = "";
            Route tmpRoute = new Route();
            List<Route> routes = new ArrayList<>();

            for (int i = 0; i < numberOfPlaces; i++){

                String tmpPlace = dis.readUTF();

                if(tmpPlace.equals(orig)) readOrigin = true;
                if(tmpPlace.equals(dest)) readDest = true;

                if (!lastPlace.equals("") && readOrigin == false){
                    Flight tmpFlight = new Flight(lastPlace,tmpPlace);
                    tmpRoute.insert(tmpFlight);
                }

                if(readDest == true) {
                    routes.add(tmpRoute.clone());
                    tmpRoute = new Route();
                }

                lastPlace = tmpPlace;
                readOrigin = false;
                readDest = false;
            }

            return routes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  @returns List<Entry<Quantity,ReserveCode>>
     */
    @Override
    public List<Map.Entry<Integer,Integer>> getMyReserves(String token) throws GetMyReservesException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request((byte) 0x8,
                baos.toByteArray().length,
                baos.toByteArray());
        Reply reply = demultiplexer.service(request);

        ByteArrayInputStream bais = new ByteArrayInputStream(reply.getData());
        DataInputStream dis = new DataInputStream(bais);

        if (reply.getError() == (byte) 0x0){
            String message = null;
            try {
                message = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new GetMyReservesException(message);
        }

        try {
            int numberOfPairs = dis.readInt();

            List<Map.Entry<Integer,Integer>> reservesCodes = new ArrayList<>();
            for (int i = 0; i < numberOfPairs; i++){

                int quant = dis.readInt();
                int code = dis.readInt();

                Map.Entry<Integer,Integer> newEntry = Map.entry(quant,code);
                reservesCodes.add(newEntry);
            }

            return reservesCodes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
