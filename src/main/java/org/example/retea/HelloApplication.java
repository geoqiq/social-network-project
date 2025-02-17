package org.example.retea;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.retea.controller.AutentificareController;
import org.example.retea.domain.validators.Validator;
import org.example.retea.repository.DataBaseRepository.*;
import org.example.retea.service.Service;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

    private AccesDatabase data;
    private UtilizatorDatabaseRepo utilizatorRepo;
    private PrietenieDatabaseRepo prietenieRepo;
    private MesajDatabaseRepo mesajRepo;
    private InvitatieDatabaseRepo invitatieRepo;
    private ParolaDatabaseRepo parolaRepo;
    public Service service;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        String url = "jdbc:postgresql://localhost:5432/lab";
        String nume_utilizator = "postgres";
        String parola = "D4t4B4s3!";

        this.data = new AccesDatabase(url, nume_utilizator, parola);
        try {
            data.creazaConexiune();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Validator valid = null;
        this.utilizatorRepo = new UtilizatorDatabaseRepo(valid,data, "users");
        this.prietenieRepo = new PrietenieDatabaseRepo(valid, data, "friendship");
        this.mesajRepo = new MesajDatabaseRepo(valid, data, "messages");
        this.invitatieRepo = new InvitatieDatabaseRepo(valid, data, "invitations");
        this.parolaRepo = new ParolaDatabaseRepo(valid, data, "parole");
        this.service = new Service(valid, utilizatorRepo, prietenieRepo, mesajRepo, invitatieRepo, parolaRepo);

        init(primaryStage);
        primaryStage.show();
    }

    private void init(Stage primaryStage) throws IOException
    {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/org/example/retea/autentificare-view.fxml"));
        AnchorPane setLayout = stageLoader.load();
        primaryStage.setTitle("Retea de socializare");
        primaryStage.setScene(new Scene(setLayout, Color.BEIGE));

        AutentificareController ctrl = stageLoader.getController();
        ctrl.setService(service);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}

