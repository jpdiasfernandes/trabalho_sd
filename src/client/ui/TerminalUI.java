package client.ui;

import client.business.AirGroup11Stub;
import client.business.Auth;
import client.business.Exceptions.LoginInvalidException;
import client.business.IAirGroup11;

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

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        System.out.println("Bem vindo ao AirGroup11!");
        this.menuPrincipal();
        System.out.println("Até breve...");
    }

    // Métodos auxiliares - Estados da UI

    private void menuPrincipal() {
        Menu menu = new Menu(new String[]{
                "Login",
                "Register",
        });

        menu.setHandler(1, ()->loginHandler());
        menu.setHandler(2, ()->registerHandler());

        // Executar o menu
        menu.run();
    }

    private void loginHandler(){
        try {
            System.out.println("username: ");
            String username = scin.nextLine();
            System.out.println("password: ");
            String password = scin.nextLine();

            String token = model.login(username,password);

            Auth auth = Auth.getInstance();
            auth.setToken(token);
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } catch (LoginInvalidException e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    private void registerHandler(){

    }
}