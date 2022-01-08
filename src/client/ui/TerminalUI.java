package client.ui;

import client.business.AirGroup11Stub;
import client.business.IAirGroup11;

import java.util.Scanner;

public class TextUI {
    // O model tem a 'lógica de negócio'.
    private IAirGroup11 model;
    // Scanner para leitura
    private Scanner scin;

    public TextUI() {
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
                "TODO",
                "TODO",
                "TODO",
                "TODO",
                "TODO",
        });

    }
}