package org.example.retea.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.retea.domain.Utilizator;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaPrieteniiController implements Observer<ChangeEvent>{

    Service service;
    String mesaj;
    List<String> prieteni;
    String numeUtil;

    @FXML
    private Button btnTrimitereMesaje;
    @FXML
    private ListView<CheckBox> listaPrieteni;
    private ObservableList<CheckBox> checkBoxList;

    public void setService(Service service, String mesaj, List<String> prieteni, String numeUtilCurent) {
        this.service = service;
        service.addObserver(this);
        this.prieteni = prieteni;
        this.mesaj = mesaj;
        this.numeUtil = numeUtilCurent;

        checkBoxList = FXCollections.observableArrayList();
        for (String numeUtil : this.prieteni) {
            CheckBox checkBox = new CheckBox(numeUtil);
            checkBox.setSelected(false);
            checkBoxList.add(checkBox);
        }
        listaPrieteni.setItems(checkBoxList);
    }

    @Override
    public void update(ChangeEvent changeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;

        checkBoxList.clear();
        for (String numeUtil : prieteni) {
            CheckBox checkBox = new CheckBox(numeUtil);
            checkBox.setSelected(false);
            checkBoxList.add(checkBox);
        }
    }

    @FXML
    private void handleUserPanelClicks(ActionEvent event)
    {
        if(event.getSource() == btnTrimitereMesaje)
            guiTrimitereMesaje();
    }

    private void guiTrimitereMesaje() {
        Long raspuns = null;
        List<CheckBox> checkBoxCopie = new ArrayList<>(checkBoxList);

        for (CheckBox checkBox : checkBoxCopie) {
            if (checkBox.isSelected()) {
                String numeUtilPrieten = checkBox.getText();

                Optional<Utilizator> prietenOptional = service.cautareNumeUtilUtilizator(numeUtilPrieten);

                if (prietenOptional.isPresent()) {
                    Utilizator prieten = prietenOptional.get();
                    Utilizator utilizCurent = service.cautareNumeUtilUtilizator(numeUtil).orElse(null);

                    if (utilizCurent != null) {
                        service.adaugareMesaj(utilizCurent.getId(), prieten.getId(), mesaj, LocalDateTime.now(), raspuns);
                    }
                }
            }
        }
        Stage stage = (Stage) btnTrimitereMesaje.getScene().getWindow();
        stage.close();
    }
}
