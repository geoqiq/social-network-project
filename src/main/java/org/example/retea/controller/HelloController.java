package org.example.retea.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.retea.domain.*;
import org.example.retea.domain.validators.ValidationException;
import org.example.retea.repository.DataBaseRepository.Page;
import org.example.retea.repository.DataBaseRepository.Pageable;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.events.StatusPrietenieType;
import org.example.retea.utils.observer.Observer;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HelloController implements Observer<ChangeEvent> {

    Service service;

    @FXML
    TableView<Utilizator> tabelUtilizatori;
    @FXML
    TableColumn<Utilizator, Long> idUtilizator;
    @FXML
    TableColumn<Utilizator, String> prenume;
    @FXML
    TableColumn<Utilizator, String> numeFam;
    @FXML
    TableColumn<Utilizator, String> numeUtil;
    ObservableList<Utilizator> utilizatoriModel = FXCollections.observableArrayList();

    @FXML
    TableView<Prietenie> tabelPrietenii;
    @FXML
    TableColumn<Prietenie, String> utiliz1;
    @FXML
    TableColumn<Prietenie, String> utiliz2;
    @FXML
    TableColumn<Prietenie, String> data;
    public ObservableList<Prietenie> prieteniiModel = FXCollections.observableArrayList();

    @FXML
    TableView<Invitatie> tabelInvitatii;
    @FXML
    TableColumn<Invitatie, Long> tabelInvitatiiUtilis1;
    @FXML
    TableColumn<Invitatie, Long> tabelInvitatiiUtiliz2;
    @FXML
    TableColumn<Invitatie, String> tabelInvitatiiStatus;
    public ObservableList<Invitatie> invitatiiModel = FXCollections.observableArrayList();

    @FXML
    private Button adaugareUser;
    @FXML
    private Button stergereUser;
    @FXML
    private Button modificareUser;
    @FXML
    private Button cautareUser;
    @FXML
    private Button adaugarePrietenie;
    @FXML
    private Button modificareInvitatie;
    @FXML
    private Button stergerePrietenie;
    @FXML
    private Button nrComunitati;
    @FXML
    private Button comunitateSociabila;

    @FXML
    private TextField inputNumeUtil;
    @FXML
    private TextField inputPrenume;
    @FXML
    private TextField inputNumeFam;
    @FXML
    private TextField inputPrieten1;
    @FXML
    private TextField inputPrieten2;
    @FXML
    private ComboBox comboStatus;
    ObservableList<StatusPrietenieType> statusuri = FXCollections.observableArrayList(StatusPrietenieType.pending, StatusPrietenieType.accepted, StatusPrietenieType.rejected);

    // paginare utilizatori
    private int paginaCurenta = 0;
    private int dimPagina = 10;
    private int nrTotalDeElemente = 0;
    @FXML
    private Label nrPagina;
    @FXML
    private Button butonNext;
    @FXML
    private Button butonPrevious;
    @FXML
    private ComboBox dimPaginaCB;
    ObservableList<Integer> elemente = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    @FXML
    private Button butonDimPagina;

    // paginare prietenii
    private int paginaCurenta2 = 0;
    private int nrTotalDeElemente2 = 0;
    @FXML
    private Label nrPagina2;
    @FXML
    private Button butonNext2;
    @FXML
    private Button butonPrevious2;

    // paginare invitatii
    private int paginaCurenta3 = 0;
    private int nrTotalDeElemente3 = 0;
    @FXML
    private Label nrPagina3;
    @FXML
    private Button butonNext3;
    @FXML
    private Button butonPrevious3;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initUtilizatoriModelPage();
        initPrieteniiModelPage();
        initInvitatiiModelPage();
    }

    @FXML
    public void initialize()
    {
        initializeTabelUtilizatori();
        initializeTabelPrietenii();
        initializeTabelInvitatii();
        comboStatus.setValue(StatusPrietenieType.pending);
        comboStatus.setItems(statusuri);
        dimPaginaCB.setItems(elemente);
        dimPaginaCB.setValue(10);
    }

    private void initializeTabelUtilizatori()
    {
        idUtilizator.setCellValueFactory(new PropertyValueFactory<Utilizator,Long>("id"));
        prenume.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("prenume"));
        numeFam.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("numeFam"));
        numeUtil.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("numeUtil"));
        tabelUtilizatori.setItems(utilizatoriModel);
    }

    private void initializeTabelPrietenii()
    {
        utiliz1.setCellValueFactory(cellData1 -> new SimpleStringProperty(((Prietenie)cellData1.getValue()).getUtilizator1().getFullName()));
        utiliz2.setCellValueFactory(cellData2 -> new SimpleStringProperty(((Prietenie)cellData2.getValue()).getUtilizator2().getFullName()));
        data.setCellValueFactory(new PropertyValueFactory<Prietenie,String>("data"));
        tabelPrietenii.setItems(prieteniiModel);
    }

    private void initializeTabelInvitatii()
    {
        tabelInvitatiiUtilis1.setCellValueFactory(new PropertyValueFactory<Invitatie,Long>("id1"));
        tabelInvitatiiUtiliz2.setCellValueFactory(new PropertyValueFactory<Invitatie,Long>("id2"));
        tabelInvitatiiStatus.setCellValueFactory(new PropertyValueFactory<Invitatie,String>("status"));
        tabelInvitatii.setItems(invitatiiModel);
    }

    private void initUtilizatoriModelPage()
    {
        Page<Utilizator> page = service.getAllUsersPage(new Pageable(paginaCurenta, dimPagina));

        int maxPage = (int) Math.ceil((double) page.getToateElementeleCount() / dimPagina) - 1;
        if(paginaCurenta > maxPage) {
            paginaCurenta = maxPage;
            page = service.getAllUsersPage(new Pageable(paginaCurenta, dimPagina));
        }

        utilizatoriModel.setAll(StreamSupport.stream(page.getElementeleDePePagina().spliterator(),
                false).collect(Collectors.toList()));
        nrTotalDeElemente = page.getToateElementeleCount();

        butonPrevious.setDisable(paginaCurenta == 0);
        butonNext.setDisable((paginaCurenta +1)* dimPagina >= nrTotalDeElemente);

        nrPagina.setText("Page " + (paginaCurenta +1) +"/" + (maxPage+1));
    }

    public void onPrevious(ActionEvent actionEvent) {
        paginaCurenta--;
        initUtilizatoriModelPage();
    }

    public void onNext(ActionEvent actionEvent) {
        paginaCurenta++;
        initUtilizatoriModelPage();
    }

    private void initPrieteniiModelPage()
    {
        Page<Prietenie> page = service.getAllFriendshipsPage(new Pageable(paginaCurenta2, dimPagina));

        int maxPage = (int) Math.ceil((double) page.getToateElementeleCount() / dimPagina) - 1;
        if(paginaCurenta2 > maxPage) {
            paginaCurenta2 = maxPage;
            page = service.getAllFriendshipsPage(new Pageable(paginaCurenta2, dimPagina));
        }

        prieteniiModel.setAll(StreamSupport.stream(page.getElementeleDePePagina().spliterator(),
                false).collect(Collectors.toList()));
        nrTotalDeElemente2 = page.getToateElementeleCount();

        butonPrevious2.setDisable(paginaCurenta2 == 0);
        butonNext2.setDisable((paginaCurenta2 +1)* dimPagina >= nrTotalDeElemente2);

        nrPagina2.setText("Page " + (paginaCurenta2 +1) +"/" + (maxPage+1));
    }

    public void onPrevious2(ActionEvent actionEvent) {
        paginaCurenta2--;
        initPrieteniiModelPage();
    }

    public void onNext2(ActionEvent actionEvent) {
        paginaCurenta2++;
        initPrieteniiModelPage();
    }

    private void initInvitatiiModelPage()
    {
        Page<Invitatie> page = service.getAllInvitationsPage(new Pageable(paginaCurenta3, dimPagina));

        int maxPage = (int) Math.ceil((double) page.getToateElementeleCount() / dimPagina) - 1;
        if(paginaCurenta3 > maxPage) {
            paginaCurenta3 = maxPage;
            page = service.getAllInvitationsPage(new Pageable(paginaCurenta3, dimPagina));
        }

        invitatiiModel.setAll(StreamSupport.stream(page.getElementeleDePePagina().spliterator(),
                false).collect(Collectors.toList()));
        nrTotalDeElemente3 = page.getToateElementeleCount();

        butonPrevious3.setDisable(paginaCurenta3 == 0);
        butonNext3.setDisable((paginaCurenta3 +1)* dimPagina >= nrTotalDeElemente3);

        nrPagina3.setText("Page " + (paginaCurenta3 +1) +"/" + (maxPage+1));
    }

    public void onPrevious3(ActionEvent actionEvent) {
        paginaCurenta3--;
        initInvitatiiModelPage();
    }

    public void onNext3(ActionEvent actionEvent) {
        paginaCurenta3++;
        initInvitatiiModelPage();
    }

    @Override
    public void update(ChangeEvent ChangeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;
        initUtilizatoriModelPage();
        initPrieteniiModelPage();
        initInvitatiiModelPage();
    }

    private Utilizator getUtilizatorSelectat()
    {
        Utilizator selectat = (Utilizator) tabelUtilizatori.getSelectionModel().getSelectedItem();
        return selectat;
    }

    private Prietenie getPrietenieSelectata()
    {
        Prietenie selectat = (Prietenie) tabelPrietenii.getSelectionModel().getSelectedItem();
        return selectat;
    }

    @FXML
    private void handleUserPanelClicks(ActionEvent event)
    {
        if(event.getSource() == stergereUser)
            guiStergereUser(getUtilizatorSelectat());
        else if(event.getSource() == adaugareUser)
            guiAdaugareUser();
        else if(event.getSource() == modificareUser)
            guiModificareUser(getUtilizatorSelectat());
        else if(event.getSource() == cautareUser)
            guiCautareUser();
        else if(event.getSource() == adaugarePrietenie)
            guiAdaugareInvitatie();
        else if(event.getSource() == modificareInvitatie)
            guiModificareInvitatie();
        else if(event.getSource() == stergerePrietenie)
            guiStergerePrietenie(getPrietenieSelectata());
        else if(event.getSource() == nrComunitati)
            guiNrComunitati();
        else if(event.getSource() == comunitateSociabila)
            guiComunitateSociabila();
        else if(event.getSource() == butonDimPagina)
            guiIncarcarePagina();
    }

    private void guiIncarcarePagina()
    {
        dimPagina = Integer.parseInt(dimPaginaCB.getValue().toString());
        initUtilizatoriModelPage();
        initPrieteniiModelPage();
        initInvitatiiModelPage();
    }

    private void guiAdaugareUser()
    {
        String numeUtil = inputNumeUtil.getText();
        String prenume = inputPrenume.getText();
        String numeFam = inputNumeFam.getText();
        Utilizator utiliz = new Utilizator(prenume, numeFam, numeUtil);
        try {
            service.adaugareUtilizator(utiliz.getPrenume(),utiliz.getNumeFam(), utiliz.getNumeUtil());
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Adaugare","Utilizatorul a fost salvat cu succes!");
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Adaugarea a esuat", e.getMessage());
        }
        inputNumeFam.setText("");
        inputPrenume.setText("");
        inputNumeUtil.setText("");
    }

    private void guiModificareUser(Utilizator selected)
    {
        String numeUtil = inputNumeUtil.getText();
        String prenume = inputPrenume.getText();
        String numeFam = inputNumeFam.getText();
        try {
            service.modificareUtilizator(prenume, numeFam, numeUtil);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION, "Modificare", "Utilizatorul a fost modificat cu succes.");
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Modificarea a esuat.", e.getMessage());
        }
        inputNumeFam.setText("");
        inputPrenume.setText("");
        inputNumeUtil.setText("");
    }

    private void guiStergereUser(Utilizator selectat)
    {
        if(selectat != null)
        {
            try {
                service.stergereUtilizator(selectat.getId());
                MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Stergere","Utilizatorul a fost sters cu succes.");
            }
            catch (ValidationException ex) {
                MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Stergerea a esuat", ex.getMessage());
            }
        }
        else MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Stergerea a esuat","Nu ati selectat niciun utilizator!");
    }

    private void guiStergerePrietenie(Prietenie selectat)
    {
        if(selectat != null)
        {
            try {
                service.stergerePrieten(selectat.getId().getLeft(), selectat.getId().getRight());
                MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Stergere","Prietenia a fost stearsa cu succes.");
            }
            catch (ValidationException ex) {
                MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Stergerea a esuat", ex.getMessage());
            }
        }
        else MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Stergerea a esuat","Nu ati selectat nicio prietenie!");
    }

    private void guiCautareUser()
    {
        String numeUtil = inputNumeUtil.getText();
        try
        {
            Optional<Utilizator> utiliz = service.cautareNumeUtilUtilizator(numeUtil);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Cautare", utiliz.get().toString());
        }
        catch (ValidationException ex) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR, "Cautarea a esuat", ex.getMessage());
        }
        inputNumeUtil.setText("");
    }

    private void guiAdaugareInvitatie()
    {
        String idString1 = inputPrieten1.getText();
        Long id1 = Long.parseLong(idString1);
        String idString2 = inputPrieten2.getText();
        Long id2 = Long.parseLong(idString2);

        StatusPrietenieType status = StatusPrietenieType.valueOf(comboStatus.getValue().toString());

        try {
            service.adaugareInvitatie(id1, id2, status);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Invitatie adaugata","Utilizatorul cu id " + id1 + " i-a trimis o cerere de prietenie utilizatorului cu id " + id2 + "!");
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Cererea de prietenie a esuat",e.getMessage());
        }

        inputPrieten1.setText("");
        inputPrieten2.setText("");
        comboStatus.setValue(StatusPrietenieType.pending);
    }

    private void guiModificareInvitatie()
    {
        String idString1 = inputPrieten1.getText();
        Long id1 = Long.parseLong(idString1);
        String idString2 = inputPrieten2.getText();
        Long id2 = Long.parseLong(idString2);

        StatusPrietenieType status = StatusPrietenieType.valueOf(comboStatus.getValue().toString());

        try {
            service.modificareInvitatie(id1, id2, status);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Modificare","Statusul cererii a fost modificat.");
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null,Alert.AlertType.ERROR,"Modificarea a esuat",e.getMessage());
        }

        inputPrieten1.setText("");
        inputPrieten2.setText("");
        comboStatus.setValue("pending");
    }

    private void guiNrComunitati()
    {
        int n = service.nrComunitati();
        MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Numar comunitati","Exista " + n + " comunitati.");
    }

    private void guiComunitateSociabila()
    {
        String s = service.comunitateSociabila();
        MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Comunitatea cea mai sociabila", s);
    }
}
