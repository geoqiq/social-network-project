package org.example.retea.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MesajAlerta {
    public static void afiseazaMesaj(Stage proprietar, Alert.AlertType tip, String header, String text){
        Alert mesaj = new Alert(tip);
        mesaj.setHeaderText(header);
        mesaj.setContentText(text);
        mesaj.initOwner(proprietar);
        mesaj.showAndWait();
    }

    public static void afiseazaMesajEroare(Stage proprietar, String text){
        Alert mesaj = new Alert(Alert.AlertType.ERROR);
        mesaj.initOwner(proprietar);
        mesaj.setTitle("Mesaj eroare");
        mesaj.setContentText(text);
        mesaj.showAndWait();
    }
}