package client.ui;

import client.business.AirGroup11Stub;
import client.business.Auth;
import client.business.Exceptions.*;
import client.business.IAirGroup11;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private void loginHandler(){
        try {
            System.out.print("\n" +Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" username > ");
            String username = scin.nextLine();
            System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" password > ");
            String password = scin.nextLine();

            //String token = model.login(username,password);

            //Auth auth = Auth.getInstance();
            //auth.setToken(token);

            home();
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
        //} catch (LoginInvalidException e) {
        //    System.out.println(e.getMessage());
        //    //e.printStackTrace();
        }
    }

    private void registerHandler(){
        System.out.println("username: ");
        String username = scin.nextLine();
        System.out.println("password: ");
        String password = scin.nextLine();

        try {
            model.register(username,password);
        } catch (RegisterInvalidException e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    private void home(){
        // TODO implements admin methods
        Menu menu = new Menu(
                "HOME",
                new String[]{
                        "Reserve travel",
                        "Cancel reserve",
                        "Get all flights",
                        "Get Routes",
                        "Notifications"
                });

        menu.setHandler(1, ()->reserveTravelHandler());
        menu.setHandler(2, ()->cancelReserveHandler());
        menu.setHandler(3, ()->getAllFlightsHandler());
        menu.setHandler(4, ()->getRoutesHandler());

        menu.run();
    }

    private void reserveTravelHandler(){

        // Get time interval
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" day(start) > ");
        int dayStart = scin.nextInt();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" month(start) > ");
        int monthStart = scin.nextInt();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" year(start) > ");
        int yearStart = scin.nextInt();

        LocalDateTime dateStart = LocalDateTime.of(yearStart,monthStart,dayStart,0,0);

        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" day(end) > ");
        int dayEnd = scin.nextInt();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" month(end) > ");
        int monthEnd = scin.nextInt();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" year(end) > ");
        int yearEnd = scin.nextInt();

        LocalDateTime dateEnd = LocalDateTime.of(yearStart,monthStart,dayStart,0,0);

        // Get places
        List<String> places = new ArrayList<>();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" origem > ");
        scin.nextLine();
        String orig = scin.nextLine();
        //System.out.println("Origem escolhida:" + orig);
        places.add(orig);

        System.out.println(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" deseja efetuar escalas? ");
        System.out.println(Colors.ANSI_YELLOW+" [0] SIM" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW+" [1] NÃO" + Colors.ANSI_RESET);
        System.out.print(Colors.ANSI_GREEN+"$"+Colors.ANSI_RESET+" Opção > "+Colors.ANSI_RESET);

        int opt = scin.nextInt();

        int i = 1;
        if (opt == 0){
            boolean contin = true;
            while(contin) {
                System.out.print(Colors.ANSI_GREEN + "$" + Colors.ANSI_RESET + " escala > ");
                scin.nextLine();
                String escala = scin.nextLine();
                //System.out.println("Escala lida:" + escala);
                places.add(escala);

                System.out.println(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" deseja efetuar mais escalas? ");
                System.out.println(Colors.ANSI_YELLOW+" [0] SIM" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_YELLOW+" [1] NÃO" + Colors.ANSI_RESET);
                System.out.print(Colors.ANSI_GREEN+"$"+Colors.ANSI_RESET+" Opção > "+Colors.ANSI_RESET);

                opt = scin.nextInt();

                if (opt == 1) contin = false;
            }
        }

        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" destino > ");
        scin.nextLine();
        String dest = scin.nextLine();
        //System.out.println("Destino lido: " + dest);
        places.add(dest);

        String token = Auth.getInstance().getToken();

        // invoke model
        /*
        try {
            model.reserveTravel(token,dateStart,dateEnd,places);
        } catch (ReserveTravelInvalidException e) {
            e.printStackTrace();
        }
        */
    }

    private void cancelReserveHandler(){
        // Get reserve code
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" reserve code > ");
        int reserveCode = scin.nextInt();
        System.out.println("Reserve code lido:" + reserveCode);

        String token = Auth.getInstance().getToken();

        // invoke model
        /*
        try {
            model.cancelReserve(token,reserveCode);
        } catch (ReserveCancelException e) {
            e.printStackTrace();
        }
         */
    }

    private void getAllFlightsHandler(){

    }

    private void getRoutesHandler(){
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" origem > ");
        //scin.nextLine();
        String orig = scin.nextLine();
        System.out.println("Orig lida:" + orig);
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" destino > ");
        //scin.nextLine();
        String dest = scin.nextLine();
        System.out.println("Dest lido:" + dest);

        String token = Auth.getInstance().getToken();

        // invoke model
        /*
        try {
            model.getRoutes(token,orig,dest);
        } catch (GetRoutesException e) {
            e.printStackTrace();
        }
         */
    }
}