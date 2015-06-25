package com.jasoncabot.gardenpath.persistence;

import javax.ws.rs.InternalServerErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InMemoryGameDao extends GameDao
{
    @Override
    protected Connection getConnection()
    {
        try
        {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'src/main/db/001_create_database.sql'");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            throw new RuntimeException("Failed to create in memory database", e);
        }
    }

    public void clear()
    {
        try (Connection conn = getConnection())
        {
            conn.prepareStatement("TRUNCATE TABLE games;").execute();
        }
        catch (SQLException e)
        {
            throw new InternalServerErrorException("Could not clear games table", e);
        }
    }
}
