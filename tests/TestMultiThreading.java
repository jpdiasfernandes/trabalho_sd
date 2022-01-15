import business.AirGroup11Stub;
import business.Exceptions.*;
import business.Flight;
import business.IAirGroup11;
import ui.Colors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

class RandomString {
    // Autoria : https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
    static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
class TestRunnable implements Runnable {
    String username;
    String password = "1234";

    public TestRunnable(String username) {
        this.username = username;
    }

    IAirGroup11 stub = new AirGroup11Stub();
    public void run() {
        try {
            stub.register(username, password);
        } catch (RegisterInvalidException e) {
            e.printStackTrace();
        }

        String token = null;
        try {
            Map.Entry<String, Boolean> me = stub.login(username, password);
            token = me.getKey();
        } catch (LoginInvalidException e) {
            e.printStackTrace();
        }

        try {
            List<Flight> lf = stub.getAllFlights(token);
            int i = 0;
            for (Flight f : lf) {
                System.out.println(
                        Colors.ANSI_GREEN + "USERNAME : " + username +
                                Colors.ANSI_YELLOW + "("+i+") "+
                                Colors.ANSI_CYAN + "(Origem) " +
                                Colors.ANSI_RESET + f.getOrig() +
                                " -> " + Colors.ANSI_CYAN + "(destino) " + Colors.ANSI_RESET + f.getDest()
                );
                i++;
            }
        } catch (GetFlightsException e) {
            e.printStackTrace();
        }

        try {
            LocalDateTime dateStart = LocalDateTime.of(2022,1,1,0,0);
            LocalDateTime dateEnd = LocalDateTime.of(2022,1,3,0,0);
            List<String> ls = new ArrayList<>();
            ls.add("Barcelona"); ls.add("Porto");
            stub.reserveTravel(token, dateStart, dateEnd, ls);
        } catch (ReserveTravelInvalidException e) {
            e.printStackTrace();
        }

        try {
            List<Map.Entry<Integer,Integer>> reserveCodes = stub.getMyReserves(token);

            for (Map.Entry<Integer,Integer> reserve: reserveCodes){
                System.out.println(Colors.ANSI_GREEN + "Code: " + Colors.ANSI_RESET + reserve.getValue() + Colors.ANSI_YELLOW +  " & " + Colors.ANSI_GREEN +  "Quantity: " + Colors.ANSI_RESET + reserve.getKey());
            }
        } catch (GetMyReservesException e) {
            e.printStackTrace();
        }
    }
}
public class TestMultiThreading {
    public static void main(String []args) {
        final int N = 150;
        Thread[] threads = new Thread[N];
        String username = "a";
        for(int i = 0; i < N; i++) {
            threads[i] = new Thread(new TestRunnable(RandomString.getAlphaNumericString(20)));
        }

        for (int i = 0; i < N; i++) {
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
