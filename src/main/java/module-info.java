module org.example.retea {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.retea to javafx.fxml;
    exports org.example.retea;

    opens org.example.retea.controller;
    exports org.example.retea.domain;
    exports org.example.retea.repository;
    exports org.example.retea.service;
    exports org.example.retea.utils.events;
    exports org.example.retea.utils.observer;
    exports org.example.retea.controller;
    exports org.example.retea.exceptions;

}