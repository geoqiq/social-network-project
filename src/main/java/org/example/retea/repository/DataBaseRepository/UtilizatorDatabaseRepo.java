package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.Utilizator;
import org.example.retea.domain.validators.Validator;
import org.example.retea.exceptions.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UtilizatorDatabaseRepo extends AbstractDatabaseRepository<Long, Utilizator> implements PagingRepository<Long, Utilizator>
{
    public UtilizatorDatabaseRepo(Validator validator, AccesDatabase data, String tabel)
    {
        super(validator, data, tabel);
    }

    private Optional<Utilizator> getUtilizator(ResultSet r, Long id) throws SQLException
    {
        String prenume = r.getString("first_name");
        String numeFam = r.getString("last_name");
        String numeUtil = r.getString("username");
        Utilizator u = new Utilizator(prenume, numeFam, numeUtil);
        u.setId(r.getLong("id"));
        obtinePrieteniPentruUtilizator(u);
        return Optional.of(u);
    }

    private void obtinePrieteniPentruUtilizator(Utilizator utilizator)
    {
        String obtinePrieteniStatement =
                "SELECT u.id, u.first_name, u.last_name, u.username " + "FROM friendship f " + "JOIN users u ON (f.id_user1 = u.id OR f.id_user2 = u.id) " + "WHERE f.id_user1 = ? OR f.id_user2 = ?";
        try
        {
            PreparedStatement stat = data.creazaStatement(obtinePrieteniStatement);
            stat.setLong(1, utilizator.getId());
            stat.setLong(2, utilizator.getId());

            ResultSet resultSet = stat.executeQuery();

            while (resultSet.next()) {
                Long prietenId = resultSet.getLong("id");
                String prenumePrieten = resultSet.getString("first_name");
                String numeFamPrieten = resultSet.getString("last_name");
                String numeUtilPrieten = resultSet.getString("username");

                Utilizator prieten = new Utilizator(prenumePrieten, numeFamPrieten, numeUtilPrieten);
                prieten.setId(prietenId);

                if(prietenId != utilizator.getId()) {
                    utilizator.getPrieteni().add(prieten);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }



    @Override
    public Optional<Utilizator> gasesteUnul(Long id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul Entity este null!"); }

        String findOneStatement = "SELECT * FROM users WHERE id = ?";
        try
        {
            PreparedStatement stat = data.creazaStatement(findOneStatement);
            stat.setLong(1, id);
            ResultSet resultSet = stat.executeQuery();
            if(resultSet.next()) {
                return getUtilizator(resultSet, id);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Utilizator> gasesteUnulNumeUtil(String numeUtil)
    {
        if (numeUtil == null) { throw new IllegalArgumentException("Numele utilizatorului este null!"); }

        String gasesteUnulStatement = "SELECT * FROM users WHERE username = ?";
        try
        {
            PreparedStatement stat = data.creazaStatement(gasesteUnulStatement);
            stat.setString(1, numeUtil);
            ResultSet r = stat.executeQuery();
            if(r.next()) {
                String prenume = r.getString("first_name");
                String numeFam = r.getString("last_name");
                Utilizator u = new Utilizator(prenume, numeFam, numeUtil);
                u.setId(r.getLong("id"));
                return Optional.of(u);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Iterable<Utilizator> gasesteToate()
    {
        String gasesteToateStatement = "SELECT * FROM users WHERE username NOT LIKE 'admin'";
        Set<Utilizator> utilizatori = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                utilizatori.add(getUtilizator(resultSet,id).get());
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return utilizatori;
    }

    public Iterable<Utilizator> gasesteTotiNeinvitatiDeID(Long ID)
    {
        String gasesteToateStatement = "\n" + "SELECT * FROM users\n" + "WHERE users.username NOT LIKE 'admin' AND users.id != ? AND users.id NOT IN ((SELECT id_user2 FROM invitations WHERE id_user1 = ?) UNION (SELECT id_user1 FROM invitations WHERE id_user2 = ?) )";
        Set<Utilizator> utilizatori = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            statement.setLong(1, ID);
            statement.setLong(2, ID);
            statement.setLong(3, ID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                utilizatori.add(getUtilizator(resultSet,id).get());
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return utilizatori;
    }

    public Iterable<Utilizator> gasesteToatePending(Long ID)
    {
        String gasesteToateStatement = "\n" + "SELECT * FROM users\n" + "WHERE users.username NOT LIKE 'admin' AND users.id != ? AND users.id IN (SELECT id_user1 FROM invitations WHERE id_user2 = ? AND status LIKE 'pending')";
        Set<Utilizator> utilizatori = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            statement.setLong(1, ID);
            statement.setLong(2, ID);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                utilizatori.add(getUtilizator(resultSet,id).get());
            }
        }
        catch (SQLException e) { throw new RuntimeException(e);}
        return utilizatori;
    }

    public Iterable<String> gasesteTotiPrieteniiPentruUtiliz(Long ID)
    {
        String gasesteToateStatement =
                "SELECT u.id, u.first_name, u.last_name, u.username " + "FROM friendship f " +
                        "JOIN users u ON (f.id_user1 = u.id OR f.id_user2 = u.id) " +
                        "WHERE f.id_user1 = ? OR f.id_user2 = ? " +
                "EXCEPT " + "SELECT u.id, u.first_name, u.last_name, u.username " + "FROM users u WHERE u.id = ? ";

        Set<String> users = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            statement.setLong(1, ID);
            statement.setLong(2, ID);
            statement.setLong(3, ID);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                users.add(getUtilizator(resultSet,id).get().getNumeUtil());
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return users;
    }

    @Override
    public Page<Utilizator> gasesteToatePage(Pageable paginabil)
    {
        String gasesteToatePageStatement = "SELECT * FROM users WHERE username NOT LIKE 'admin' LIMIT ? OFFSET ?";
        String counter = "SELECT COUNT(*) AS count FROM users";
        List<Utilizator> utilizatori = new ArrayList<>();
        try
        {
            PreparedStatement stat = data.creazaStatement(gasesteToatePageStatement);
            stat.setInt(1, paginabil.getDimensiunePagina());
            stat.setInt(2, paginabil.getDimensiunePagina() * paginabil.getNumarPagina());

            ResultSet resultSet = stat.executeQuery();

            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                utilizatori.add(getUtilizator(resultSet,id).get());
            }

            PreparedStatement statementcount = data.creazaStatement(counter);
            ResultSet resultSetCount = statementcount.executeQuery();
            int totalCounter = 0;
            if(resultSetCount.next()) {
                totalCounter = resultSetCount.getInt("count");
            }
            return new Page<>(utilizatori, totalCounter);
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Utilizator> salveaza(Utilizator entitate)
    {
        String insertSQL = "INSERT INTO users(first_name,last_name, username) values(?,?,?)";
        try
        {
            PreparedStatement stat = data.creazaStatement(insertSQL);
            stat.setString(1,entitate.getPrenume());
            stat.setString(2,entitate.getNumeFam());
            stat.setString(3, entitate.getNumeUtil());
            int response = stat.executeUpdate();
            return response == 0?Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    @Override
    public  Optional<Utilizator> sterge(Long id)
    {
        Optional<Utilizator> entity = this.gasesteUnul(id);
        if(id != null)
        {
            String deleteStatement = "DELETE FROM users where id="+id;
            int raspuns = 0;
            try
            {
                PreparedStatement statement = data.creazaStatement(deleteStatement);

                if (entity.isPresent()) {
                    raspuns = statement.executeUpdate();
                }
                return raspuns == 0? Optional.empty() : entity;
            }
            catch (SQLException e) { throw new RuntimeException(e); }
        }
        else { throw  new IllegalArgumentException("ID-ul nu poate fi null!"); }
    }

    @Override
    public Optional<Utilizator> actualizeaza(Utilizator entitate)
    {
        if (entitate == null) { throw new RepositoryException("Entity nu poate fi null!"); }
        String updateStatement = "UPDATE users SET first_name=?, last_name=? WHERE username=?";
        try
        {
            PreparedStatement statement = data.creazaStatement(updateStatement);
            statement.setString(1, entitate.getPrenume());
            statement.setString(2, entitate.getNumeFam());
            statement.setString(3, entitate.getNumeUtil());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entitate) : Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }
}
