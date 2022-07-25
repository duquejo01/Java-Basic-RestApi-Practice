package co.duquejo.api;

import co.duquejo.api.services.CatsService;

import javax.swing.*;
import java.util.ArrayList;

public class CatsApi {
    public static void main(String[] args) {

        int selectedOption = -1;

        ArrayList<String> options = new ArrayList<>();
        options.add("Show cats");
        options.add("Show favorites");
        options.add("Exit");

        do {

            // Main menu
            Object panel = JOptionPane.showInputDialog(
                null,
                "Java Cats",
                "Main menu",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options.toArray(),
                options.get(0)
            );

            selectedOption = options.indexOf( panel );
            switch(  selectedOption ) {
                case 0:
                    CatsService.getCats();
                    break;
                case 1:
                    CatsService.getFavorites();
                    break;
                default:
                    selectedOption = -1;
                    break;
            }
        } while ( selectedOption != -1 );
    }
}
