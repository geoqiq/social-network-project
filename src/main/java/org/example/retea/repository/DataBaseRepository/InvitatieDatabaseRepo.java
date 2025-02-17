package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.*;
import org.example.retea.domain.validators.Validator;
import org.example.retea.exceptions.RepositoryException;
import org.example.retea.utils.events.StatusPrietenieType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InvitatieDatabaseRepo implements PagingRepository<Tuplu<Long,Long>, Invitatie>
{
    protected Validator validator;
    protected AccesDatabase data;
    protected String tabel;

    public InvitatieDatabaseRepo(Validator validator, AccesDatabase data, String tabel)
    {
        this.validator = validator;
        this.data = data;
        this.tabel = tabel;
    }

    private Invitatie getInvitatieDinStatement(ResultSet resultSet) throws SQLException
    {
        Long id1 = resultSet.getLong("id_user1");
        Long id2 = resultSet.getLong("id_user2");
        String statusString = resultSet.getString("status");
        StatusPrietenieType status = StatusPrietenieType.valueOf(statusString);

        Invitatie inv = new Invitatie(id1, id2, status);
        return inv;
    }

    public Iterable<Invitatie> findAllByReceiverId(Long id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul entitatii este null!"); }
        String gasesteToateStatement = "SELECT * FROM invitations" + " WHERE (id_user2=?);";
        Set<Invitatie> invitatii = new HashSet<>();
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteToateStatement);
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Invitatie i = getInvitatieDinStatement(resultSet);
                invitatii.add(i);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return invitatii;
    }

    public Iterable<Invitatie> gasesteToate()
    {
        String findAllStatement="SELECT * FROM invitations";
        Set<Invitatie> invitatii = new HashSet<>();
        try
        {
            PreparedStatement statement= data.creazaStatement(findAllStatement);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()) {
                Invitatie i = getInvitatieDinStatement(resultSet);
                invitatii.add(i);
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return invitatii;
    }

    public Optional<Invitatie> gasesteUnul(Tuplu<Long,Long> id)
    {
        if (id == null) { throw new IllegalArgumentException("Id-ul entitatii este null!"); }
        String gasesteUnulStatement = "SELECT * FROM invitations" + " WHERE (id_user1=? and id_user2=?);";
        try
        {
            PreparedStatement statement = data.creazaStatement(gasesteUnulStatement);
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Invitatie inv = getInvitatieDinStatement(resultSet);
                return Optional.of(inv);
            }
            return Optional.empty();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Page<Invitatie> gasesteToatePage(Pageable paginabil)
    {
        String gasesteToatePageStatement = "SELECT * FROM invitations LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) AS count FROM invitations";
        List<Invitatie> inv = new ArrayList<>();
        try
        {
            PreparedStatement statement= data.creazaStatement(gasesteToatePageStatement);
            statement.setInt(1, paginabil.getDimensiunePagina());
            statement.setInt(2, paginabil.getDimensiunePagina() * paginabil.getNumarPagina());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
                inv.add(getInvitatieDinStatement(resultSet));

            PreparedStatement statementCount = data.creazaStatement(count);
            ResultSet resultSetCount = statementCount.executeQuery();
            int totalCounter = 0;
            if(resultSetCount.next()) {
                totalCounter = resultSetCount.getInt("count");
            }
            return new Page<>(inv, totalCounter);
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Invitatie> salveaza(Invitatie entitate)
    {
        String insertSQL = "INSERT INTO invitations(id_user1, id_user2, status) values(?,?,?)";
        try {
            PreparedStatement statement= data.creazaStatement(insertSQL);
            statement.setLong(1, entitate.getId1());
            statement.setLong(2,entitate.getId2());
            statement.setString(3,entitate.getStatus());
            int response = statement.executeUpdate();
            return response == 0?Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    public Optional<Invitatie> actualizeaza(Invitatie entitate)
    {
        if (entitate == null) {
            throw new RepositoryException("Entity nu poate sa fie null");
        }
        String updateStatement = "UPDATE invitations set status=?" +
                " where (id_user1=? and id_user2=?);";
        try {
            PreparedStatement statement = data.creazaStatement(updateStatement);
            statement.setString(1,entitate.getStatus());
            statement.setLong(2,entitate.getId1());
            statement.setLong(3,entitate.getId2());
            int response = statement.executeUpdate();
            return response == 0? Optional.of(entitate):Optional.empty();
        }
        catch (SQLException e) { throw new RepositoryException(e); }
    }

    @Override
    public Optional<Invitatie> sterge(Tuplu<Long, Long> longLongTuplu)
    {
        return Optional.empty();
    }
}
