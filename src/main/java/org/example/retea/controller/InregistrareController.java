package org.example.retea.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.retea.domain.Parola;
import org.example.retea.domain.Utilizator;
import org.example.retea.domain.validators.ValidationException;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.observer.Observer;

import java.util.Optional;

public class InregistrareController implements Observer<ChangeEvent> {

    Service service;
    @FXML
    private Button butonSInregistrare;
    @FXML
    private TextField inputNumeUtil;
    @FXML
    private PasswordField inputParola;
    @FXML
    private PasswordField inputParola2;
    @FXML
    private TextField inputNumeFam;
    @FXML
    private TextField inputPrenume;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    @Override
    public void update(ChangeEvent changeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;
    }

    @FXML
    private void handleUserPanelClicks(ActionEvent event)
    {
        if(event.getSource() == butonSInregistrare)
            inregistrareGUI();
    }

    private void inregistrareGUI() {
        String numeFam = inputNumeFam.getText();
        String prenume = inputPrenume.getText();
        String numeUtil = inputNumeUtil.getText();
        String parola = inputParola.getText();
        String parola2 = inputParola2.getText();

        if(numeFam.isEmpty() || prenume.isEmpty() || numeUtil.isEmpty() || parola.isEmpty() || parola2.isEmpty()) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Eroare", "Toate campurile sunt obligatorii!");
            return;
        }

        if(!parola.equals(parola2)) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Eroare", "Parolele nu corespund!");
            inputParola.setText("");
            inputParola2.setText("");
        } else {
            try
            {
                Optional<Utilizator> utiliz = service.cautareNumeUtilUtilizator(numeUtil);
                if(utiliz.isPresent()){
                    MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Eroare", "Exista deja cineva cu acest nume de utilizator.");
                    inputNumeUtil.setText("");
                    inputParola.setText("");
                    inputParola2.setText("");
                }
                else
                {
                    try {
                        service.adaugareUtilizator(numeFam, prenume, numeUtil);
                        if(service.cautareNumeUtilUtilizator(numeUtil).isPresent())
                        {
                            Parola p = new Parola(parola, numeUtil);
                            String parola_criptata = p.criptare();
                            service.adaugare_parola(numeUtil, parola_criptata);
                        }
                        MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION, "Contul a fost creat!", "Inchide fereastra pentru a te intoarce la Autentificare.");
                    } catch (ValidationException e) {
                        MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Crearea contrului a esuat.", e.getMessage());
                    }
                }
            }
            catch (ValidationException ex) {
                MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Crearea contului a esuat.",ex.getMessage());
            }
        }

        inputNumeFam.setText("");
        inputPrenume.setText("");
        inputNumeUtil.setText("");
        inputParola.setText("");
        inputParola2.setText("");
    }
}
