package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.*;
import org.example.retea.domain.validators.Validator;
import org.example.retea.exceptions.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class MesajDatabaseRepo implements PagingRepository<Long, Mesaj>{

    protected Validator validator;
    protected AccesDatabase data;
    protected String tabel;

    public MesajDatabaseRepo(Validator validator, AccesDatabase data, String tabel) {
        this.validator = validator;
        this.data = data;
        this.tabel = tabel;
    }

    private Mesaj getMessageFromStatement(ResultSet resultSet) throws SQLException
    {
        Long mesajId = resultSet.getLong("id");
        Long emitatorId = resultSet.getLong("sender_id");
        Long receptorId = resultSet.getLong("receiver_id");
        String mesaj = resultSet.getString("message");
        Timestamp data = resultSet.getTimestamp("data");
        Long raspuns = resultSet.getLong("raspuns_la");

        Mesaj m = new Mesaj(emitatorId, receptorId, mesaj, data.toLocalDateTime());
        m.setId(mesajId);
        m.setRaspuns(raspuns);
        return m;
    }

    public Optional<Mesaj> gasesteUnul(Tuplu<Long,Long> id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul lui Entity este null!"); }
        String findOneStatement = "SELECT * FROM  messages" + " WHERE (sender_id=? and receiver_id=?);";
        try
        {
            PreparedStatement statement = data.creazaStatement(findOneStatement);
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Mesaj msg = getMessageFromStatement(resultSet);
                return Optional.of(msg);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Mesaj> gaseste(Long id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul lui Entity este null!"); }
        String findOneStatement = "SELECT * FROM  messages" + " WHERE id=?;";
        try
        {
            PreparedStatement statement = data.creazaStatement(findOneStatement);
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Mesaj msg = getMessageFromStatement(resultSet);
                return Optional.of(msg);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Mesaj> gasesteUnul(Long id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul lui Entity este null!"); }
        String gasesteUnulStatement = "SELECT * FROM messages  WHERE id = ?";
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteUnulStatement);
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Mesaj m  = getMessageFromStatement(resultSet);
                return Optional.of(m);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Iterable<Mesaj> gasesteToate()
    {
        String gasesteToateStatement = "SELECT * FROM messages";
        Set<Mesaj> mesaje = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()) {
               Mesaj m = getMessageFromStatement(resultSet);
               mesaje.add(m);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return mesaje;
    }

    @Override
    public Page<Mesaj> gasesteToatePage(Pageable paginabil)
    {
        String gasesteToatePageStatement = "SELECT * FROM messages LIMIT ? OFFSET ?";
        String counter = "SELECT COUNT(*) AS count FROM messages";
        List<Mesaj> mesaj = new ArrayList<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToatePageStatement);
            statement.setInt(1, paginabil.getDimensiunePagina());
            statement.setInt(2, paginabil.getDimensiunePagina() * paginabil.getNumarPagina());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
                mesaj.add(getMessageFromStatement(resultSet));

            PreparedStatement statementCounter = data.creazaStatement(counter);
            ResultSet resultSetCount = statementCounter.executeQuery();
            int totalCounter = 0;
            if(resultSetCount.next()) {
                totalCounter = resultSetCount.getInt("count");
            }
            return new Page<>(mesaj, totalCounter);
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Mesaj> salveaza(Mesaj entitate)
    {
        String insertSQL = "INSERT INTO messages(sender_id,receiver_id, message, data, raspuns_la) values(?,?,?,?,?)";
        try
        {
            PreparedStatement statement = data.creazaStatement(insertSQL);
            statement.setLong(1,entitate.getDela());
            statement.setLong(2,entitate.getCatre());
            statement.setString(3,entitate.getMesaj());
            statement.setTimestamp(4, Timestamp.valueOf(entitate.getData()));
            if(entitate.getRaspuns() != null)
                statement.setLong(5,entitate.getRaspuns());
            else
                statement.setLong(5,-1);
            int raspuns = statement.executeUpdate();
            return raspuns == 0?Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    @Override
    public Optional<Mesaj> sterge(Long aLong) {
        return Optional.empty();
    }
    public  Optional<Mesaj> sterge(Tuplu<Long,Long> id)
    {
        Optional<Mesaj> entity = gasesteUnul(id);
        if(id != null)
        {
            String deleteStatement = "DELETE FROM messages where (sender_id=? and receiver_id=?)";
            int response=0;
            try
            {
                PreparedStatement statement = data.creazaStatement(deleteStatement);
                statement.setLong(1,id.getLeft());
                statement.setLong(2,id.getRight());

                if (entity.isPresent()) { response = statement.executeUpdate(); }
                return response == 0? Optional.empty() : entity;

            }
            catch (SQLException e) { throw new RuntimeException(e); }
        }
        else { throw  new IllegalArgumentException("ID-ul nu poate sa fie null!"); }
    }

    public Optional<Mesaj> actualizeaza(Mesaj entitate)
    {
        if (entitate == null) { throw new RepositoryException("Entity nu poate sa fie null!"); }
        String updateStatement = "UPDATE messages set message=?" + " where (sender_id=? and receiver_id=?);";
        try {
            PreparedStatement statement = data.creazaStatement(updateStatement);
            statement.setString(1,entitate.getMesaj());
            statement.setLong(2,entitate.getDela());
            statement.setLong(3,entitate.getCatre());
            int response=statement.executeUpdate();
            return response==0? Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    public Iterable<Mesaj> conversatiiUtilizatori(Long id_utiliz1, Long id_utiliz2)
    {
        String gasesteToateStatement = "SELECT * FROM messages WHERE (sender_id=? and receiver_id=?) or (sender_id=? and receiver_id=?) ORDER BY data";
        List<Mesaj> mesaje = new ArrayList<>();
        try
        {
            PreparedStatement statement= data.creazaStatement(gasesteToateStatement);
            statement.setLong(1,id_utiliz1);
            statement.setLong(2,id_utiliz2);
            statement.setLong(3,id_utiliz2);
            statement.setLong(4,id_utiliz1);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()) {
                Mesaj m = getMessageFromStatement(resultSet);
                mesaje.add(m);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return mesaje;
    }
}
