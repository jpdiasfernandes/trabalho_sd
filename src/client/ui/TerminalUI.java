package ui;

import business.*;
import business.Exceptions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TerminalUI {
    // O model tem a 'lógica de negócio'.
    private IAirGroup11 model;
    // Scanner para leitura
    private Scanner scin;

    public TerminalUI() {
        this.model = new AirGroup11Stub();
        scin = new Scanner(System.in);
    }

    public void run() {
        this.menuPrincipal();
    }

    private void menuPrincipal() {
        Menu menu = new Menu(
                "INÍCIO",
                new String[]{
                    "Login",
                    "Register",
                });

        menu.setHandler(1, ()->loginHandler());
        menu.setHandler(2, ()->registerHandler());

        menu.run();
    }

    // DONE
    private void loginHandler(){
        try {
            System.out.print("\n" +Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" username > ");
            String username = scin.nextLine();
            System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" password > ");
            String password = scin.nextLine();

            Map.Entry<String,Boolean> loginAnswer = model.login(username,password);

            //System.out.println("Obtida reply e o token é: " + loginAnswer.getKey());

            Auth auth = Auth.getInstance();
            auth.setToken(loginAnswer.getKey());
            auth.setIsAdmin(loginAnswer.getValue());

            home();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        } catch (LoginInvalidException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }
    // DONE
    private void registerHandler(){
        System.out.print("\n" +Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" username > ");
        String username = scin.nextLine();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" password > ");
        String password = scin.nextLine();

        try {
            model.register(username,password);
        } catch (RegisterInvalidException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }

    private void home(){
        Auth auth = Auth.getInstance();
        Menu menu = null;

        if (!auth.isAdmin()) {
            menu = new Menu(
                    "HOME",
                    new String[]{
                            "Reserve travel",
                            "Cancel reserve",
                            "Get all flights",
                            "Get Routes",
                            "My reserves"
                    });
        }else{
            menu = new Menu(
                    "HOME",
                    new String[]{
                            "Reserve travel",
                            "Cancel reserve",
                            "Get all flights",
                            "Get Routes",
                            "My Reserves",
                            "Insert flight",
                            "Cancel day"
                    });
        }

        menu.setHandler(1, ()->reserveTravelHandler());
        menu.setHandler(2, ()->cancelReserveHandler());
        menu.setHandler(3, ()->getAllFlightsHandler());
        menu.setHandler(4, ()->getRoutesHandler());
        menu.setHandler(5, ()->getMyReserversHandler());

        if (auth.isAdmin()){
            menu.setHandler(6, ()->insertFlightHandler());
            menu.setHandler(7, ()->cancelDayHandler());
        }

        menu.run();
    }

    /* Regular client handlers */

    private void reserveTravelHandler(){

        // Get time interval
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" day(start) > ");
        String line = scin.nextLine();
        int dayStart = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+ " month(start) > ");
        line = scin.nextLine();
        int monthStart = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" year(start) > ");
        line = scin.nextLine();
        int yearStart = Integer.parseInt(line);

        LocalDateTime dateStart = LocalDateTime.of(yearStart,monthStart,dayStart,0,0);

        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" day(end) > ");
        line = scin.nextLine();
        int dayEnd = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" month(end) > ");
        line = scin.nextLine();
        int monthEnd = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" year(end) > ");
        line = scin.nextLine();
        int yearEnd = Integer.parseInt(line);

        LocalDateTime dateEnd = LocalDateTime.of(yearEnd,monthEnd,dayEnd,0,0);

        // Get places
        List<String> places = new ArrayList<>();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" origem > ");
        String orig = scin.nextLine();
        //System.out.println("Origem escolhida:" + orig);
        places.add(orig);

        System.out.println(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" deseja efetuar escalas? ");
        System.out.println(Colors.ANSI_YELLOW+" [0] SIM" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW+" [1] NÃO" + Colors.ANSI_RESET);
        System.out.print(Colors.ANSI_GREEN+"$"+Colors.ANSI_RESET+" Opção > "+Colors.ANSI_RESET);

        line = scin.nextLine();
        int opt = Integer.parseInt(line);

        int i = 1;
        if (opt == 0){
            boolean contin = true;
            while(contin) {
                System.out.print(Colors.ANSI_GREEN + "$" + Colors.ANSI_RESET + " escala > ");
                String escala = scin.nextLine();
                //System.out.println("Escala lida:" + escala);
                places.add(escala);

                System.out.println(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" deseja efetuar mais escalas? ");
                System.out.println(Colors.ANSI_YELLOW+" [0] SIM" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_YELLOW+" [1] NÃO" + Colors.ANSI_RESET);
                System.out.print(Colors.ANSI_GREEN+"$"+Colors.ANSI_RESET+" Opção > "+Colors.ANSI_RESET);


                line = scin.nextLine();
                opt = Integer.parseInt(line);

                if (opt == 1) contin = false;
            }
        }

        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" destino > ");
        String dest = scin.nextLine();
        //System.out.println("Destino lido: " + dest);
        places.add(dest);

        String token = Auth.getInstance().getToken();

        System.out.println("\nO sistema está a processar sua reserva ...\n" +
                "Caso for bem sucedida, encontrará dentro de alguns instantes o código de sua reserva na secção " +
                Colors.ANSI_GREEN + "my reserves" + Colors.ANSI_RESET);

        new Thread(() -> {
            try {
                int reserveCode = model.reserveTravel(token,dateStart,dateEnd,places);
                //System.out.println("Código da reserva: " + reserveCode);
            } catch (ReserveTravelInvalidException e) {
                //System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
                //e.printStackTrace();
            }

        }).start();
    }
    private void cancelReserveHandler(){
        // Get reserve code
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" reserve code > ");

        String line = scin.nextLine();
        int reserveCode = Integer.parseInt(line);
        System.out.println("Reserve code lido:" + reserveCode);

        String token = Auth.getInstance().getToken();

        try {
            model.cancelReserve(token,reserveCode);
        } catch (ReserveCancelException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }
    private void getAllFlightsHandler(){
        Auth auth = Auth.getInstance();

        try {
            List<Flight> allFlights = model.getAllFlights(auth.getToken());
            int i = 0;
            System.out.print("\n");
            for (Flight flight: allFlights){
                System.out.println(
                        Colors.ANSI_YELLOW + "("+i+") "+
                        Colors.ANSI_CYAN + "(Origem) " +
                        Colors.ANSI_RESET + flight.getOrig() +
                        " -> " + Colors.ANSI_CYAN + "(destino) " + Colors.ANSI_RESET + flight.getDest()
                );
                i++;
            }
        } catch (GetFlightsException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }
    private void getRoutesHandler(){
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" origem > ");
        //scin.nextLine();
        String orig = scin.nextLine();
        //System.out.println("Orig lida:" + orig);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" destino > ");
        //scin.nextLine();
        String dest = scin.nextLine();
        //System.out.println("Dest lido:" + dest);

        String token = Auth.getInstance().getToken();

        try {
            List<Route> routes = model.getRoutes(token,orig,dest);

            int i = 0;
            System.out.print("\n");
            for(Route route: routes){
                System.out.println(Colors.ANSI_PURPLE + "[" + i + "]" + Colors.ANSI_RESET + "Percurso: ");
                int j = 0;
                for (Flight flight: route.getRoute()) {
                    System.out.println(
                            Colors.ANSI_YELLOW + "(voo " + j + ") " +
                                    Colors.ANSI_CYAN + "(Origem) " +
                                    Colors.ANSI_RESET + flight.getOrig() +
                                    " -> " + Colors.ANSI_CYAN + "(destino) " + Colors.ANSI_RESET + flight.getDest()
                    );
                    j++;
                }
                i++;
            }
        } catch (GetRoutesException e) {
             System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
        }
    }
    private void getMyReserversHandler(){
        Auth auth = Auth.getInstance();

        try {
            List<Map.Entry<Integer,Integer>> reserveCodes = model.getMyReserves(auth.getToken());

            System.out.print("\n");
            for (Map.Entry<Integer,Integer> reserve: reserveCodes){
                System.out.println(Colors.ANSI_GREEN + "Code: " + Colors.ANSI_RESET + reserve.getValue() + Colors.ANSI_YELLOW +  " & " + Colors.ANSI_GREEN +  "Quantity: " + Colors.ANSI_RESET + reserve.getKey());
            }
            //System.out.println("\n");

        } catch (GetMyReservesException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
        }
    }

    /* Admin handlers */
    private void insertFlightHandler(){
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" origem > ");
        String orig = scin.nextLine();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" destino > ");
        String destin = scin.nextLine();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" capacidade > ");
        String line = scin.nextLine();
        Short capacity = Short.parseShort(line);

        Auth auth = Auth.getInstance();

        try {
            model.insertFlight(auth.getToken(),orig,destin,capacity);
        } catch (InsertFlightInvalidException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }
    private void cancelDayHandler(){
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" day(start) > ");
        String line = scin.nextLine();
        int day = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" month(start) > ");
        line = scin.nextLine();
        int month = Integer.parseInt(line);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" year(start) > ");
        line = scin.nextLine();
        int year = Integer.parseInt(line);
        //scin.nextLine();

        LocalDateTime date = LocalDateTime.of(year,month,day,0,0);

        Auth auth = Auth.getInstance();

        try {
            model.cancelDay(auth.getToken(),date);
        } catch (CancelDayInvalidException e) {
            System.out.println(Colors.ANSI_YELLOW + "[ERROR] " + Colors.ANSI_RESET + e.getMessage());
            //e.printStackTrace();
        }
    }
}