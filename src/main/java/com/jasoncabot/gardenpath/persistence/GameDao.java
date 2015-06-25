package com.jasoncabot.gardenpath.persistence;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.PrivateInfo;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameDao
{
    private static final Logger logger = Logger.getLogger(GameDao.class);

    private static final String JNDI_DATASOURCE_NAME = "jdbc/GardenpathDB";

    protected Connection getConnection()
    {
        try
        {
            final Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            final DataSource ds = (DataSource) ctx.lookup(JNDI_DATASOURCE_NAME);
            return ds.getConnection();
        }
        catch (NamingException | SQLException e)
        {
            throw new InternalServerErrorException(
                    "Failed to lookup datasource. Ensure that application server has datasource '" + JNDI_DATASOURCE_NAME + "' defined", e);
        }
    }

    public Stream<GameMemento> findAll(final String state)
    {
        final Stream.Builder<GameMemento> builder = Stream.builder();
        try (Connection conn = getConnection())
        {
            final PreparedStatement findAllGames = conn.prepareStatement("SELECT id, " + GameMemento.FIELDS_FOR_SELECTION + " FROM games WHERE state = ?;");
            findAllGames.setString(1, state);
            findAllGames.execute();
            try (final ResultSet resultSet = findAllGames.getResultSet())
            {
                while (resultSet.next())
                {
                    final GameMemento memento = getMemento(resultSet);
                    // build it!
                    builder.accept(memento);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for finding all public games. ", e);
        }

        return builder.build();
    }

    public GameMemento findPublicGameInState(long gameId, String state)
    {
        try (Connection conn = getConnection())
        {
            final PreparedStatement findGame = conn.prepareStatement("SELECT id, " + GameMemento.FIELDS_FOR_SELECTION + " FROM games WHERE id = ? AND state = ? AND name IS NULL and hashed_password IS NULL;");
            findGame.setLong(1, gameId);
            findGame.setString(2, state);
            findGame.execute();
            try (final ResultSet resultSet = findGame.getResultSet())
            {
                if (resultSet.next())
                {
                    return getMemento(resultSet);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for finding a particular game", e);
        }
        throw new NotFoundException("Could not find game with id " + gameId);
    }

    public GameMemento findPrivateGameInState(final PrivateInfo info, final String state)
    {
        try (Connection conn = getConnection())
        {
            final PreparedStatement findGame = conn.prepareStatement("SELECT id, " + GameMemento.FIELDS_FOR_SELECTION + " FROM games WHERE name = ? AND hashed_password = ? AND state = ?;");
            findGame.setString(1, info.getName());
            findGame.setString(2, info.getHashedPassword());
            findGame.setString(3, state);
            findGame.execute();
            try (final ResultSet resultSet = findGame.getResultSet())
            {
                if (resultSet.next())
                {
                    return getMemento(resultSet);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for finding a particular game", e);
        }
        throw new NotFoundException("Could not find matching game");
    }

    public GameMemento find(final long gameId, final String playerId, final String state)
    {
        try (Connection conn = getConnection())
        {
            final PreparedStatement findGame = conn.prepareStatement(
                    "SELECT id, " + GameMemento.FIELDS_FOR_SELECTION + " FROM games WHERE id = ? AND (p1_id = ? OR p2_id = ?) AND state = ?");
            findGame.setLong(1, gameId);
            findGame.setString(2, playerId);
            findGame.setString(3, playerId);
            findGame.setString(4, state);
            findGame.execute();
            try (final ResultSet resultSet = findGame.getResultSet())
            {
                if (resultSet.next())
                {
                    return getMemento(resultSet);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for finding a particular game", e);
        }
        throw new NotFoundException("Could not find matching game");
    }

    public GameMemento find(long gameId, String playerId)
    {
        try (Connection conn = getConnection())
        {
            final PreparedStatement findGame = conn.prepareStatement("SELECT id, " + GameMemento.FIELDS_FOR_SELECTION + " FROM games WHERE id = ? AND (p1_id = ? OR p2_id = ?)");
            findGame.setLong(1, gameId);
            findGame.setString(2, playerId);
            findGame.setString(3, playerId);
            findGame.execute();
            try (final ResultSet resultSet = findGame.getResultSet())
            {
                if (resultSet.next())
                {
                    return getMemento(resultSet);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for finding a particular game", e);
        }
        throw new NotFoundException("Could not find matching game");
    }

    private GameMemento getMemento(final ResultSet resultSet) throws SQLException
    {
        final GameMemento memento = new GameMemento();
        // game info
        memento.setId(resultSet.getLong("id"));
        memento.setState(resultSet.getString("state"));
        memento.setLastMoveAt(resultSet.getTimestamp("last_move_at").toInstant());
        memento.setName(resultSet.getString("name"));
        memento.setHashedPassphrase(resultSet.getString("hashed_password"));
        memento.setIsPlayer1Turn(resultSet.getBoolean("is_player_one_turn"));
        // player 1
        memento.setPlayer1Id(resultSet.getString("p1_id"));
        memento.setPlayer1Name(resultSet.getString("p1_name"));
        memento.setPlayer1Position(resultSet.getInt("p1_position"));
        memento.setPlayer1Fence1(resultSet.getInt("p1_f1"));
        memento.setPlayer1Fence2(resultSet.getInt("p1_f2"));
        memento.setPlayer1Fence3(resultSet.getInt("p1_f3"));
        memento.setPlayer1Fence4(resultSet.getInt("p1_f4"));
        memento.setPlayer1Fence5(resultSet.getInt("p1_f5"));
        memento.setPlayer1Fence6(resultSet.getInt("p1_f6"));
        memento.setPlayer1Fence7(resultSet.getInt("p1_f7"));
        memento.setPlayer1Fence8(resultSet.getInt("p1_f8"));
        memento.setPlayer1Fence9(resultSet.getInt("p1_f9"));
        memento.setPlayer1Fence10(resultSet.getInt("p1_f10"));
        // player 2
        memento.setPlayer2Id(resultSet.getString("p2_id"));
        memento.setPlayer2Name(resultSet.getString("p2_name"));
        memento.setPlayer2Position(resultSet.getInt("p2_position"));
        memento.setPlayer2Fence1(resultSet.getInt("p2_f1"));
        memento.setPlayer2Fence2(resultSet.getInt("p2_f2"));
        memento.setPlayer2Fence3(resultSet.getInt("p2_f3"));
        memento.setPlayer2Fence4(resultSet.getInt("p2_f4"));
        memento.setPlayer2Fence5(resultSet.getInt("p2_f5"));
        memento.setPlayer2Fence6(resultSet.getInt("p2_f6"));
        memento.setPlayer2Fence7(resultSet.getInt("p2_f7"));
        memento.setPlayer2Fence8(resultSet.getInt("p2_f8"));
        memento.setPlayer2Fence9(resultSet.getInt("p2_f9"));
        memento.setPlayer2Fence10(resultSet.getInt("p2_f10"));
        return memento;
    }

    public void save(final Game game)
    {
        if (game.getId() == null)
        {
            game.setId(insert(GameMemento.fromGame(game)));
        }
        else
        {
            update(GameMemento.fromGame(game));
        }
    }

    private void update(final GameMemento memento)
    {
        try (Connection conn = getConnection())
        {
            final String updates = Stream.of(GameMemento.UPDATEABLE_FIELDS)
                    .map(name -> name + "=?")
                    .collect(Collectors.joining(", "));

            final PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games SET " + updates + " WHERE id = ?;");
            updatePreparedStatement(memento, preparedStatement);
            preparedStatement.setLong(32, memento.getId());
            if (preparedStatement.executeUpdate() != 1)
            {
                logger.warn("Failed to update memento. " + memento);
                throw new InternalServerErrorException("Game not updated");
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for updating game", e);
        }
    }

    private long insert(final GameMemento memento)
    {
        long id = 0;
        try (Connection conn = getConnection())
        {
            final String fields = GameMemento.FIELDS_FOR_SELECTION;
            final String values = Stream.of(GameMemento.UPDATEABLE_FIELDS).map(name -> "?").collect(Collectors.joining(", "));

            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO games (" + fields + ") VALUES (" + values + ");"
                    , Statement.RETURN_GENERATED_KEYS);
            updatePreparedStatement(memento, preparedStatement);
            preparedStatement.executeUpdate();

            try (final ResultSet rs = preparedStatement.getGeneratedKeys())
            {
                if (rs.next())
                {
                    id = rs.getLong(1);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("Could not execute sql for inserting game", e);
        }

        return id;
    }

    private void updatePreparedStatement(GameMemento memento, PreparedStatement preparedStatement) throws SQLException
    {
        preparedStatement.setString(1, memento.getState());
        preparedStatement.setTimestamp(2, Timestamp.from(memento.getLastMoveAt()));
        preparedStatement.setString(3, memento.getName());
        preparedStatement.setString(4, memento.getHashedPassphrase());
        preparedStatement.setBoolean(5, memento.isPlayer1Turn());
        preparedStatement.setString(6, memento.getPlayer1Id());
        preparedStatement.setString(7, memento.getPlayer1Name());
        preparedStatement.setInt(8, memento.getPlayer1Position());
        preparedStatement.setInt(9, memento.getPlayer1Fence1());
        preparedStatement.setInt(10, memento.getPlayer1Fence2());
        preparedStatement.setInt(11, memento.getPlayer1Fence3());
        preparedStatement.setInt(12, memento.getPlayer1Fence4());
        preparedStatement.setInt(13, memento.getPlayer1Fence5());
        preparedStatement.setInt(14, memento.getPlayer1Fence6());
        preparedStatement.setInt(15, memento.getPlayer1Fence7());
        preparedStatement.setInt(16, memento.getPlayer1Fence8());
        preparedStatement.setInt(17, memento.getPlayer1Fence9());
        preparedStatement.setInt(18, memento.getPlayer1Fence10());
        preparedStatement.setString(19, memento.getPlayer2Id());
        preparedStatement.setString(20, memento.getPlayer2Name());
        preparedStatement.setInt(21, memento.getPlayer2Position());
        preparedStatement.setInt(22, memento.getPlayer2Fence1());
        preparedStatement.setInt(23, memento.getPlayer2Fence2());
        preparedStatement.setInt(24, memento.getPlayer2Fence3());
        preparedStatement.setInt(25, memento.getPlayer2Fence4());
        preparedStatement.setInt(26, memento.getPlayer2Fence5());
        preparedStatement.setInt(27, memento.getPlayer2Fence6());
        preparedStatement.setInt(28, memento.getPlayer2Fence7());
        preparedStatement.setInt(29, memento.getPlayer2Fence8());
        preparedStatement.setInt(30, memento.getPlayer2Fence9());
        preparedStatement.setInt(31, memento.getPlayer2Fence10());
    }
}
