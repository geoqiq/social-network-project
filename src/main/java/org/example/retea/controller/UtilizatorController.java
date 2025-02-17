package org.example.retea.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.retea.domain.Mesaj;
import org.example.retea.domain.Utilizator;
import org.example.retea.domain.validators.ValidationException;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.events.StatusPrietenieType;
import org.example.retea.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorController implements Observer<ChangeEvent> {

    Service service;
    String numeUtil;

    @FXML
    private Label labelNumeUtil;
    @FXML
    private TableView<Utilizator> tabelPrietenii;
    @FXML
    private TableColumn<Utilizator, String> colNumeUtil;
    @FXML
    private TableColumn<Utilizator, String> colStatus;
    public ObservableList<Utilizator> prieteniiModel = FXCollections.observableArrayList();

    @FXML
    private TableView<Utilizator> TabelInvitatii;
    @FXML
    private TableColumn<Utilizator, String> colNumeUtilInvitat;
    @FXML
    private TableColumn<Utilizator, String> colInvita;
    public ObservableList<Utilizator> invitatiiModel = FXCollections.observableArrayList();

    @FXML
    private Button butonDeschidereConversatie;
    @FXML
    private ComboBox prieteniCB;
    @FXML
    private TextArea textAreaMesaje;
    @FXML
    private Button butonTrimiteMesaj;
    @FXML
    private TextArea inputMesaj;
    @FXML
    private TextField inputIdMesajRaspuns;
    @FXML
    private Button butonTrimiteMesaje;


    public void setService(Service service, String numeUtil) {
        this.service = service;
        service.addObserver(this);
        this.labelNumeUtil.setText("Bun venit, " + numeUtil + "!");
        this.numeUtil = numeUtil;
        initPrieteniModel();
        initInvitatiiModel();
        ObservableList<String> prieteni = FXCollections.observableArrayList(getPrieteniPentruUtilizator(numeUtil));
        prieteniCB.setItems(prieteni);
        textAreaMesaje.setEditable(false);
    }

    @Override
    public void update(ChangeEvent changeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;
        initPrieteniModel();
        initInvitatiiModel();
        ObservableList<String> prieteni = FXCollections.observableArrayList(getPrieteniPentruUtilizator(numeUtil));
        prieteniCB.setItems(prieteni);
        guiDeschidereConversatie();
    }
    @FXML
    public void initialize() {
        initializeTabelPrieteni();
        initializeazaTabelInvitatii();
    }
    private void initializeTabelPrieteni() {
        colNumeUtil.setCellValueFactory(new PropertyValueFactory<>("numeUtil"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("numeUtil"));

        colStatus.setCellFactory(param -> new TableCell<>() {
            private final Button butonAccept = new Button("Accept");
            private final Button butonReject = new Button("Reject");
            {
                butonAccept.setStyle("-fx-background-color: #00FF00;");
                butonAccept.setOnAction(event -> handleButonAccept(getIndex()));
                butonReject.setStyle("-fx-background-color: #FF0000;");
                butonReject.setOnAction(event -> handleButonReject(getIndex()));
            }

            @Override
            protected void updateItem(String obiect, boolean gol) {
                super.updateItem(obiect, gol);

                if (gol || obiect == null) {
                    setGraphic(null);
                } else {
                    HBox buttonBox = new HBox(5, butonAccept, butonReject);
                    buttonBox.setAlignment(Pos.CENTER);
                    setGraphic(buttonBox);
                }
            }
        });
        tabelPrietenii.setItems(prieteniiModel);
    }

    private void handleButonAccept(int index) {
        Optional<Utilizator> currentuser = service.cautareNumeUtilUtilizator(numeUtil);
        if (index >= 0 && index < prieteniiModel.size()) {
            Utilizator selectedItem = prieteniiModel.get(index);
            service.modificareInvitatie(selectedItem.getId(), currentuser.get().getId(), StatusPrietenieType.accepted);
            initPrieteniModel();
            initInvitatiiModel();
            ObservableList<String> prieteni = FXCollections.observableArrayList(getPrieteniPentruUtilizator(numeUtil));
            prieteniCB.setItems(prieteni);
        }
    }

    private void handleButonReject(int index) {
        Optional<Utilizator> currentuser = service.cautareNumeUtilUtilizator(numeUtil);
        if (index >= 0 && index < prieteniiModel.size()) {
            Utilizator selectedItem = prieteniiModel.get(index);
            service.modificareInvitatie(selectedItem.getId(), currentuser.get().getId(), StatusPrietenieType.rejected);
            initPrieteniModel();
            initInvitatiiModel();
            ObservableList<String> prieteni = FXCollections.observableArrayList(getPrieteniPentruUtilizator(numeUtil));
            prieteniCB.setItems(prieteni);
        }
    }

    private void initPrieteniModel() {
        Optional<Utilizator> utilizatorOptional = service.cautareNumeUtilUtilizator(numeUtil);
        if (utilizatorOptional.isPresent()) {
            Utilizator currentUser = utilizatorOptional.get();

            Iterable<Utilizator> utilizatori = service.cautareUtilizatoriPending(currentUser.getId());
            List<Utilizator> List = StreamSupport.stream(utilizatori.spliterator(),false).collect(Collectors.toList());
            prieteniiModel.setAll(List);
        }
    }

    private void initializeazaTabelInvitatii() {
        colNumeUtilInvitat.setCellValueFactory(new PropertyValueFactory<>("numeUtil"));
        colInvita.setCellValueFactory(new PropertyValueFactory<>("numeUtil"));
        colInvita.setCellFactory(param -> new TableCell<>() {
            private final Button inviteButton = new Button("Invite");
            {
                inviteButton.setOnAction(event -> handleButonInvitatie(getIndex()));
            }

            @Override
            protected void updateItem(String obiect, boolean gol) {
                super.updateItem(obiect, gol);

                if (gol || obiect == null) {
                    setGraphic(null);
                } else {
                    HBox buttonBox = new HBox(5, inviteButton);
                    buttonBox.setAlignment(Pos.CENTER);
                    setGraphic(buttonBox);
                }
            }
        });
        TabelInvitatii.setItems(invitatiiModel);
    }

    private void handleButonInvitatie(int index) {
        Optional<Utilizator> currentuser = service.cautareNumeUtilUtilizator(numeUtil);
        if (index >= 0 && index < invitatiiModel.size()) {
            Utilizator selectedItem = invitatiiModel.get(index);
            service.adaugareInvitatie(currentuser.get().getId(), selectedItem.getId(), StatusPrietenieType.pending);
            initInvitatiiModel();
            initPrieteniModel();
            ObservableList<String> prieteni = FXCollections.observableArrayList(getPrieteniPentruUtilizator(numeUtil));
            prieteniCB.setItems(prieteni);
        }
    }

    private void initInvitatiiModel() {
        Optional<Utilizator> utilizatorOptional = service.cautareNumeUtilUtilizator(numeUtil);
        if (utilizatorOptional.isPresent()) {
            Utilizator currentUser = utilizatorOptional.get();

            Iterable<Utilizator> utilizatori = service.cautareUtilizatoriNeinvitati(currentUser.getId());
            List<Utilizator> List = StreamSupport.stream(utilizatori.spliterator(),false).collect(Collectors.toList());
            invitatiiModel.setAll(List);
        }
    }

    private List<String> getPrieteniPentruUtilizator(String username)
    {
        Optional<Utilizator> utilizatorOptional = service.cautareNumeUtilUtilizator(username);
        Utilizator currentUser = utilizatorOptional.get();
        Iterable<String> lista = service.getFriendsForUser(currentUser.getId());
        return StreamSupport.stream(lista.spliterator(),false).collect(Collectors.toList());
    }

    @FXML
    private void handleMessagePanelClicks(ActionEvent event)
    {
        if(event.getSource()== butonDeschidereConversatie)
            guiDeschidereConversatie();
        if(event.getSource()== butonTrimiteMesaj)
            guiAdaugareMesaj();
        if(event.getSource()== butonTrimiteMesaje)
            guiAfisarePrieteniTrimitereMesaj();
    }

    private void guiDeschidereConversatie()
    {
        String username_prieten;
        Object obj_username_prieten = prieteniCB.getValue();
        if(obj_username_prieten != null)
        {
            username_prieten = obj_username_prieten.toString();
            Optional<Utilizator> prietenoptional = service.cautareNumeUtilUtilizator(username_prieten);

            Utilizator prieten = prietenoptional.get();
            Utilizator currentuser = service.cautareNumeUtilUtilizator(numeUtil).get();
            Iterable<Mesaj> conversatie = service.afisareListaMesaje(currentuser.getId(), prieten.getId());
            List<Mesaj> lista_mesaje = StreamSupport.stream(conversatie.spliterator(),false).collect(Collectors.toList());
            String mesaje = "";
            for(int i = 0; i< lista_mesaje.size(); i++) {

                Long id =  lista_mesaje.get(i).getDela();
                String from = service.cautareUtilizator(id).get().getNumeUtil();

                mesaje += "(id: " + lista_mesaje.get(i).getId() + ") " + from + ": " + lista_mesaje.get(i).getMesaj();

                Long id_reply = lista_mesaje.get(i).getRaspuns();
                if(id_reply!=-1) {
                    Mesaj reply_mesaj = service.cautareMesaj(id_reply).get();
                    mesaje += " {a raspuns la: " + reply_mesaj.getMesaj() + "}";
                }
                mesaje += "\n";
            }
            textAreaMesaje.setText(mesaje);
        }
    }

    private void guiAdaugareMesaj()
    {
        String text = inputMesaj.getText();
        Long raspuns = null;
        String stringIdMesaj = inputIdMesajRaspuns.getText();
        try
        {
            raspuns = Long.parseLong(stringIdMesaj);

            Object objNumeUtilPrieten = prieteniCB.getValue();
            if(objNumeUtilPrieten != null) {
                String numeUtilPrieten = objNumeUtilPrieten.toString();
                if (numeUtilPrieten != null) {
                    Optional<Utilizator> prietenOptional = service.cautareNumeUtilUtilizator(numeUtilPrieten);
                    if (prietenOptional.isPresent()) {
                        Utilizator prieten = prietenOptional.get();
                        Utilizator utilizCurent = service.cautareNumeUtilUtilizator(numeUtil).get();
                        if (raspuns != null) {
                            boolean verif = verificaIdMesaj(raspuns, utilizCurent, prieten);
                            if (verif == true)
                                service.adaugareMesaj(utilizCurent.getId(), prieten.getId(), text, LocalDateTime.now(), raspuns);
                        } else
                            service.adaugareMesaj(utilizCurent.getId(), prieten.getId(), text, LocalDateTime.now(), raspuns);
                        guiDeschidereConversatie();
                    }
                }
            }
            else
                MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Eroare","Nu ati deschis nicio conversatie.");
        }
        catch (NumberFormatException e) {
            MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Eroare", "Nu ati introdus un numar valid.");
        }

        inputMesaj.setText("");
        inputIdMesajRaspuns.setText("");
    }

    private boolean verificaIdMesaj(Long id, Utilizator curent, Utilizator prieten)
    {
        try {
            Mesaj mes = service.cautareMesaj(id).get();
            if((mes.getCatre() == curent.getId() && mes.getDela() == prieten.getId()) || (mes.getCatre() == prieten.getId() && mes.getDela() == curent.getId()))
                return true;
            else
            {
                MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Eroare","Nu exista mesajul cu id-ul dat in conversatie.");
                return false;
            }
        }
        catch (ValidationException e)
        {
            MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Mesajul nu exista.",e.getMessage());
            return false;
        }
    }

    private void guiAfisarePrieteniTrimitereMesaj() {
        String mesaj = inputMesaj.getText();
        List<String> listaPrieteni = getPrieteniPentruUtilizator(numeUtil);
        if (listaPrieteni.isEmpty())
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Eroare", "Nu aveti niciun prieten!");
        else if (mesaj.equals(""))
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Eroare","Nu ati introdus niciun mesaj.");
        else {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/org/example/retea/lista-prietenii-view.fxml"));
                Parent friendsListView = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Selecteaza prieteni");
                stage.setScene(new Scene(friendsListView));

                ListaPrieteniiController ctrl = loader.getController();
                ctrl.setService(service, mesaj, listaPrieteni, numeUtil);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        inputMesaj.setText("");
    }
}
