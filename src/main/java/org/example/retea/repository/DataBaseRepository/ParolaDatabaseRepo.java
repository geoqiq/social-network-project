package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.*;
import org.example.retea.domain.validators.Validator;
import org.example.retea.exceptions.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ParolaDatabaseRepo implements PagingRepository<Long, Parola>
{
    protected Validator validator;
    protected AccesDatabase data;
    protected String tabel;

    public ParolaDatabaseRepo(Validator validator, AccesDatabase data, String tabel)
    {
        this.validator = validator;
        this.data = data;
        this.tabel = tabel;
    }

    private Parola getParolaDinStatement(ResultSet resultSet) throws SQLException
    {
        Long id = resultSet.getLong("id");
        String numeUtil = resultSet.getString("username");
        String parola = resultSet.getString("parola");
        Parola p = new Parola(parola, numeUtil);
        p.setId(id);
        return p;
    }

    public Optional<Parola> gasesteUnul(Tuplu<String,String> id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul Entity este null!"); }
        String gasesteUnulStatement = "SELECT * FROM parole" + " WHERE (username=? and parola=?);";
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteUnulStatement);
            statement.setString(1,id.getLeft());
            statement.setString(2,id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Parola p = getParolaDinStatement(resultSet);
                return Optional.of(p);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Parola> gasesteUnul(Long aLong) {
        return Optional.empty();
    }

    public Iterable<Parola> gasesteToate()
    {
        String findAllStatement = "SELECT * FROM parole";
        Set<Parola> parole = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(findAllStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Parola p = getParolaDinStatement(resultSet);
                parole.add(p);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return parole;
    }

    public Optional<Parola> salveaza(Parola entitate)
    {
        String insertSQL = "INSERT INTO parole(username, parola) VALUES(?, ?)";
        try (PreparedStatement stat = data.creazaStatement(insertSQL))
        {
            stat.setString(1, entitate.getNumeUtil());
            stat.setString(2, entitate.getParola());

            int raspuns = stat.executeUpdate();
            if (raspuns > 0) {
                return Optional.empty();
            } else {
                return Optional.of(entitate);
            }
        } catch (SQLException e) { throw new RepositoryException("Eroare in salvarea entitatii Parola.", e); }
    }

    @Override
    public Optional<Parola> sterge(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Parola> actualizeaza(Parola entitate) {
        return Optional.empty();
    }

    @Override
    public Page<Parola> gasesteToatePage(Pageable paginabil) {
        return null;
    }
}

