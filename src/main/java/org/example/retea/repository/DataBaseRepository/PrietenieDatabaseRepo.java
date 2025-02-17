package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.Prietenie;
import org.example.retea.domain.Tuplu;
import org.example.retea.domain.Utilizator;
import org.example.retea.domain.validators.Validator;
import org.example.retea.exceptions.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class PrietenieDatabaseRepo extends AbstractDatabaseRepository<Tuplu<Long, Long>, Prietenie> implements PagingRepository<Tuplu<Long, Long>, Prietenie>
{
    public PrietenieDatabaseRepo(Validator validator, AccesDatabase data, String tabel)
    {
        super(validator, data, tabel);
    }

    private Prietenie getPrietenieDinStatement(ResultSet resultSet) throws SQLException
    {
        Long idUtiliz1 = resultSet.getLong("id_user1");
        Long idUtiliz2 = resultSet.getLong("id_user2");
        String prenume1 = resultSet.getString("firstNameU1");
        String numeFam1 = resultSet.getString("lastNameU1");
        String prenume2 = resultSet.getString("firstNameU2");
        String numeFam2 = resultSet.getString("lastNameU2");

        Utilizator utilizator1 = new Utilizator(prenume1, numeFam1, "username1");
        utilizator1.setId(idUtiliz1);
        Utilizator utilizator2 = new Utilizator(prenume2, numeFam2, "username2");
        utilizator2.setId(idUtiliz2);
        Timestamp inceputPrietenie = resultSet.getTimestamp("friendsfrom");
        Prietenie prietenie = new Prietenie(utilizator1, utilizator2, inceputPrietenie.toLocalDateTime());
        return prietenie;
    }

    @Override
    public Optional<Prietenie> gasesteUnul(Tuplu<Long,Long> id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul lui Entity este null!"); }
        String gasesteUnulStatement = "SELECT * FROM GetFriendshipInformation()" + " WHERE (id_user1=? and id_user2=?)" + " or (id_user2=? and id_user1=?) ;";
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteUnulStatement);
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            statement.setLong(3,id.getLeft());
            statement.setLong(4,id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Prietenie prietenie = getPrietenieDinStatement(resultSet);
                return Optional.of(prietenie);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Iterable<Prietenie> gasesteToate()
    {
        String gasesteToateStatement = "SELECT * FROM GetFriendshipInformation()";
        Set<Prietenie> prietenii = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Prietenie friendship = getPrietenieDinStatement(resultSet);
                prietenii.add(friendship);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return prietenii;
    }

    @Override
    public Page<Prietenie> gasesteToatePage(Pageable pageable)
    {
        String gasesteToatePageStatement = "SELECT * FROM GetFriendshipInformation() LIMIT ? OFFSET ?";
        String counter = "SELECT COUNT(*) AS count FROM GetFriendshipInformation()";
        List<Prietenie> prietenii = new ArrayList<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToatePageStatement);
            statement.setInt(1, pageable.getDimensiunePagina());
            statement.setInt(2, pageable.getDimensiunePagina() * pageable.getNumarPagina());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
                prietenii.add(getPrietenieDinStatement(resultSet));

            PreparedStatement statementcount = data.creazaStatement(counter);
            ResultSet resultSetCount = statementcount.executeQuery();
            int totalCounter = 0;
            if(resultSetCount.next()) {
                totalCounter = resultSetCount.getInt("count");
            }
            return new Page<>(prietenii, totalCounter);
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Prietenie> sterge(Tuplu<Long,Long> id)
    {
        Optional<Prietenie> entity = gasesteUnul(id);
        if(id != null)
        {
            String deleteStatement = "DELETE FROM friendship where (id_user1=? and id_user2=?) or (id_user2=? and id_user1=?)";
            int raspuns = 0;
            try
            {
                PreparedStatement statement = data.creazaStatement(deleteStatement);
                statement.setLong(1,id.getLeft());
                statement.setLong(2,id.getRight());
                statement.setLong(3,id.getLeft());
                statement.setLong(4,id.getRight());

                if (entity.isPresent()) {
                    raspuns = statement.executeUpdate();
                }
                return raspuns == 0? Optional.empty() : entity;

            }
            catch (SQLException e) { throw new RuntimeException(e); }
        }
        else { throw new IllegalArgumentException("ID-ul nu poate fi nul!"); }
    }

    @Override
    public Optional<Prietenie> salveaza(Prietenie entitate)
    {
        String insertDB_SQL = "INSERT INTO friendship(id_user1,id_user2,friendsfrom) values(?,?,?)";
        try
        {
            PreparedStatement statement = data.creazaStatement(insertDB_SQL);
            statement.setLong(1,entitate.getId().getLeft());
            statement.setLong(2,entitate.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(entitate.getData()));
            int response = statement.executeUpdate();
            return response == 0?Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    @Override
    public Optional<Prietenie> actualizeaza(Prietenie entitate)
    {
        if (entitate == null) { throw new RepositoryException("Entity nu poate fi null!"); }
        String actualizeazaStatement = "UPDATE friends set friendsfrom=?" + " where (id_user1=? and id_user2=?)" + "or (id_user2=? and id_user1=?)";
        try {
            PreparedStatement statement = data.creazaStatement(actualizeazaStatement);
            statement.setTimestamp(1,Timestamp.valueOf(entitate.getData()));
            statement.setLong(2,entitate.getId().getLeft());
            statement.setLong(3,entitate.getId().getRight());
            statement.setLong(4,entitate.getId().getLeft());
            statement.setLong(5,entitate.getId().getRight());
            int raspuns = statement.executeUpdate();
            return raspuns == 0? Optional.of(entitate):Optional.empty();
        } catch (SQLException e) { throw new RepositoryException(e); }
    }
}