package ui;

import business.AirGroup11Stub;
import business.Auth;
import business.Exceptions.*;
import business.IAirGroup11;

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

    private void loginHandler(){
        try {
            System.out.print("\n" +Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" username > ");
            String username = scin.nextLine();
            System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" password > ");
            String password = scin.nextLine();

            Map.Entry<String,Boolean> loginAnswer = model.login(username,password);

            System.out.println("Obtida reply e o token é: " + loginAnswer.getKey());

            Auth auth = Auth.getInstance();
            auth.setToken(loginAnswer.getKey());
            auth.setIsAdmin(loginAnswer.getValue());

            home();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        } catch (LoginInvalidException e) {
            System.out.print("Deu erro ... mensagem: ");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    private void registerHandler(){
        System.out.print("\n" +Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" username > ");
        String username = scin.nextLine();
        System.out.print(Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" password > ");
        String password = scin.nextLine();

        try {
            model.register(username,password);
        } catch (RegisterInvalidException e) {
            System.out.print("Deu erro ... mensagem: ");
            System.out.println(e.getMessage());
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

    // regular client handlers
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

        try {
            int reserveCode = model.reserveTravel(token,dateStart,dateEnd,places);
            System.out.println("Código da reserva: " + reserveCode);
        } catch (ReserveTravelInvalidException e) {
            System.out.println("Deu erro ... mensagem: ");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }
    private void cancelReserveHandler(){
        // Get reserve code
        System.out.print("\n" + Colors.ANSI_GREEN + "$"+Colors.ANSI_RESET+" reserve code > ");
        int reserveCode = scin.nextInt();
        System.out.println("Reserve code lido:" + reserveCode);

        String token = Auth.getInstance().getToken();

        try {
            model.cancelReserve(token,reserveCode);
        } catch (ReserveCancelException e) {
            System.out.println("Deu erro ... mensagem: ");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
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
    private void getMyReserversHandler(){

    }

    // admin handlers
    private void insertFlightHandler(){

    }
    private void cancelDayHandler(){

    }
}