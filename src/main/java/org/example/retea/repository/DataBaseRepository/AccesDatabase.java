package org.example.retea.repository.DataBaseRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccesDatabase {

    private Connection conexiune;
    private final String urlAcces, parolaAcces, numeUtilDatabase;

    public AccesDatabase(String urlAcces, String numeUtilDatabase, String parolaAcces)
    {
        this.urlAcces = urlAcces;
        this.numeUtilDatabase = numeUtilDatabase;
        this.parolaAcces = parolaAcces;
    }

    public void creazaConexiune() throws SQLException { conexiune = DriverManager.getConnection(urlAcces, numeUtilDatabase, parolaAcces);}
    public PreparedStatement creazaStatement(String statement) throws SQLException { return conexiune.prepareStatement(statement);}
}
