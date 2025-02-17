package org.example.retea.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.retea.domain.Parola;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.observer.Observer;

import java.io.IOException;

public class AutentificareController implements Observer<ChangeEvent> {

    Service service;
    @FXML
    private Button btnAutentificare;
    @FXML
    private Button btnInregistrare;
    @FXML
    private TextField inputNumeUtil;
    @FXML
    private PasswordField inputParola;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    @FXML
    private void handleUserPanelClicks(ActionEvent event)
    {
        if(event.getSource() == btnAutentificare)
            autentificareGUI();
        else if(event.getSource() == btnInregistrare)
           inregistrareGUI();
    }

    @Override
    public void update(ChangeEvent changeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;
    }

    private void autentificareGUI()
    {
        String numeUtil = inputNumeUtil.getText();
        String parolaUtil = inputParola.getText();

        if(numeUtil.isEmpty() || parolaUtil.isEmpty()) {
            MesajAlerta.afiseazaMesaj(null, javafx.scene.control.Alert.AlertType.ERROR, "EROARE", "Toate campurile sunt obligatorii!");
            return;
        }

        Parola parola = new Parola(inputParola.getText(), inputNumeUtil.getText());
        Parola parolaCriptata = new Parola(parola.criptare(), inputNumeUtil.getText());

        try {
            service.cautareParola(parolaCriptata);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION, "Autentificare reusita.", "");
            fereastraUtilGUI(inputNumeUtil.getText());
        }
        catch (RuntimeException e) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Autentificare esuata.", e.getMessage());
        }

        inputNumeUtil.setText("");
        inputParola.setText("");
    }

    private void fereastraUtilGUI(String numeUtil)
    {
        if(numeUtil.equals("admin"))
        {
            try
            {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/org/example/retea/hello-view.fxml"));
                Parent adminView = loader.load();

                Stage adminStage = new Stage();
                adminStage.setTitle("Administrator retea");
                adminStage.setScene(new Scene(adminView));

                HelloController startController = loader.getController();
                startController.setService(service);
                adminStage.show();

                FXMLLoader loaderMsg = new FXMLLoader();
                loaderMsg.setLocation(getClass().getResource("/org/example/retea/mesaj-view.fxml"));
                Parent adminViewMsg = loaderMsg.load();

                Stage adminmsgstage = new Stage();
                adminmsgstage.setTitle("Administrator retea");
                adminmsgstage.setScene(new Scene(adminViewMsg));

                MesajController msgController = loaderMsg.getController();
                msgController.setService(service);
                adminmsgstage.show();

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/org/example/retea/utilizator-view.fxml"));
                Parent inregistrareView = loader.load();

                Stage inregistrareStage = new Stage();
                inregistrareStage.setTitle("Bun venit!");
                inregistrareStage.setScene(new Scene(inregistrareView));

                UtilizatorController utilizatorController = loader.getController();
                utilizatorController.setService(service, numeUtil);
                inregistrareStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void inregistrareGUI()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/retea/inregistrare-view.fxml"));
            Parent inregistrareView = loader.load();

            Stage signupStage = new Stage();
            signupStage.setTitle("Creeaza un cont");
            signupStage.setScene(new Scene(inregistrareView));

            InregistrareController inregistrareController = loader.getController();
            inregistrareController.setService(service);
            signupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
