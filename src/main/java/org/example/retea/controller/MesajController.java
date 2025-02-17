package org.example.retea.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.retea.domain.Mesaj;
import org.example.retea.domain.validators.ValidationException;
import org.example.retea.repository.DataBaseRepository.Page;
import org.example.retea.repository.DataBaseRepository.Pageable;
import org.example.retea.service.Service;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MesajController implements Observer<ChangeEvent> {

    Service service;

    @FXML
    TableView<Mesaj> tabelMesaj;
    @FXML
    TableColumn<Mesaj, Long> mesajId;
    @FXML
    TableColumn<Mesaj, Long> emitatorId;
    @FXML
    TableColumn<Mesaj, Long> receptorId;
    @FXML
    TableColumn<Mesaj, String> text;
    @FXML
    TableColumn<Mesaj, String> data;
    @FXML
    TableColumn<Mesaj, Long> raspuns;

    @FXML
    private Button butonTrimitereMesaj;
    @FXML
    private Button butonAfisareMesaje;

    @FXML
    private TextArea inputText;
    @FXML
    private TextField inputEmitator;
    @FXML
    private TextField inputReceptor;
    @FXML
    private TextField inputRaspuns;

    @FXML
    private TextField inputUser1;
    @FXML
    private TextField inputUser2;
    public ObservableList<Mesaj> mesajeModel = FXCollections.observableArrayList();


    // pentru paginare mesaje
    private int paginaCurenta4 = 0;
    private int dimPagina4 = 10;
    private int nrTotalDeElemente4 = 0;
    @FXML
    private Label nrPagina4;
    @FXML
    private Button butonNext4;
    @FXML
    private Button butonPrevious4;
    @FXML
    private Button butonDimPagina;
    @FXML
    private ComboBox dimPaginaCB;
    ObservableList<Integer> elemente = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initMessageModelPage();
        dimPaginaCB.setItems(elemente);
        dimPaginaCB.setValue(10);
    }

    @FXML
    public void initialize()
    {
        initializeTableMessages();
    }

    private void initializeTableMessages()
    {
        mesajId.setCellValueFactory(new PropertyValueFactory<Mesaj,Long>("id"));
        emitatorId.setCellValueFactory(new PropertyValueFactory<Mesaj,Long>("dela"));
        receptorId.setCellValueFactory(new PropertyValueFactory<Mesaj,Long>("catre"));
        text.setCellValueFactory(new PropertyValueFactory<Mesaj,String>("mesaj"));
        data.setCellValueFactory(new PropertyValueFactory<Mesaj,String>("data"));
        raspuns.setCellValueFactory(new PropertyValueFactory<Mesaj,Long>("raspuns"));
        tabelMesaj.setItems(mesajeModel);
    }

    private void initMessageModelPage()
    {
        Page<Mesaj> pagina = service.getAllMessagesPage(new Pageable(paginaCurenta4, dimPagina4));

        int maxPagina = (int) Math.ceil((double) pagina.getToateElementeleCount() / dimPagina4) - 1;
        if(paginaCurenta4 > maxPagina) {
            paginaCurenta4 = maxPagina;
            pagina = service.getAllMessagesPage(new Pageable(paginaCurenta4, dimPagina4));
        }

        mesajeModel.setAll(StreamSupport.stream(pagina.getElementeleDePePagina().spliterator(),
                false).collect(Collectors.toList()));
        nrTotalDeElemente4 = pagina.getToateElementeleCount();

        butonPrevious4.setDisable(paginaCurenta4 == 0);
        butonNext4.setDisable((paginaCurenta4 +1)* dimPagina4 >= nrTotalDeElemente4);

        nrPagina4.setText("Page " + (paginaCurenta4 +1) +"/" + (maxPagina+1));
    }

    public void onPrevious4(ActionEvent actionEvent) {
        paginaCurenta4--;
        initMessageModelPage();
    }

    public void onNext4(ActionEvent actionEvent) {
        paginaCurenta4++;
        initMessageModelPage();
    }

    @Override
    public void update(ChangeEvent ChangeEvent, Service serviceActualizat) {
        this.service = serviceActualizat;
        initMessageModelPage();
    }

    @FXML
    private void handleMessagePanelClicks(ActionEvent event)
    {
        if(event.getSource() == butonTrimitereMesaj)
            guiAdaugareMesaj();
        else if(event.getSource() == butonAfisareMesaje)
            guiAfisareMesaje();
        else if(event.getSource() == butonDimPagina)
            guiIncarcarePagina();
    }

    private void guiIncarcarePagina()
    {
        dimPagina4 = Integer.parseInt(dimPaginaCB.getValue().toString());
        initMessageModelPage();
    }

    private void guiAdaugareMesaj()
    {
        String idString1 = inputEmitator.getText();
        Long id_emitator = Long.parseLong(idString1);
        String idString2 = inputReceptor.getText();
        String[] receptori = idString2.split(" ");
        String text = inputText.getText();
        String idString3 = inputRaspuns.getText();
        Long raspuns;
        if(idString3 == "")
            raspuns = null;
        else
            raspuns = Long.parseLong(idString3);
        try {
            for(int i = 0; i < receptori.length; ++i) {
                service.adaugareMesaj(id_emitator, Long.parseLong(receptori[i]), text, LocalDateTime.now(), raspuns);
            }

            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Adaugare","Mesajul a fost salvat cu succes!");
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Adaugarea a esuat.",e.getMessage());
        }

        inputEmitator.setText("");
        inputReceptor.setText("");
        inputText.setText("");
        inputRaspuns.setText("");
    }

    private void guiAfisareMesaje()
    {
        String idString1 = inputUser1.getText();
        Long idUtiliz1 = Long.parseLong(idString1);
        String idString2 = inputUser2.getText();
        Long idUtiliz2 = Long.parseLong(idString2);

        try {
            String str = service.afisareMesaje(idUtiliz1, idUtiliz2);
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.CONFIRMATION,"Afisare conversatii",str);
        } catch (ValidationException e) {
            MesajAlerta.afiseazaMesaj(null, Alert.AlertType.ERROR,"Afisarea a esuat.",e.getMessage());
        }
    }
}
