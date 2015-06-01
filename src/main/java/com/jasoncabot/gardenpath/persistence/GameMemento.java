package com.jasoncabot.gardenpath.persistence;

import java.util.Date;

public class GameMemento
{

    private static final String DEFAULT_STATE = "UNKNOWN";

    private long id;
    private String name;
    private String player1Name;
    private String player2Name;
    private String hashedPassphrase;
    private String player1Id;
    private String player2Id;
    private int player1Position;
    private int player2Position;
    private int player1Fence1;
    private int player1Fence2;
    private int player1Fence3;
    private int player1Fence4;
    private int player1Fence5;
    private int player1Fence6;
    private int player1Fence7;
    private int player1Fence8;
    private int player1Fence9;
    private int player1Fence10;
    private int player2Fence1;
    private int player2Fence2;
    private int player2Fence3;
    private int player2Fence4;
    private int player2Fence5;
    private int player2Fence6;
    private int player2Fence7;
    private int player2Fence8;
    private int player2Fence9;
    private int player2Fence10;
    private String state;
    private Date lastMoveAt;
    private Boolean isPlayer1Turn;

    public GameMemento()
    {
        state = DEFAULT_STATE;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getPlayer1Name()
    {
        return player1Name;
    }

    public String getPlayer2Name()
    {
        return player2Name;
    }

    public String getHashedPassphrase()
    {
        return hashedPassphrase;
    }

    public String getPlayer1Id()
    {
        return player1Id;
    }

    public String getPlayer2Id()
    {
        return player2Id;
    }

    public int getPlayer1Position()
    {
        return player1Position;
    }

    public int getPlayer2Position()
    {
        return player2Position;
    }

    public int getPlayer1Fence1()
    {
        return player1Fence1;
    }

    public int getPlayer1Fence2()
    {
        return player1Fence2;
    }

    public int getPlayer1Fence3()
    {
        return player1Fence3;
    }

    public int getPlayer1Fence4()
    {
        return player1Fence4;
    }

    public int getPlayer1Fence5()
    {
        return player1Fence5;
    }

    public int getPlayer1Fence6()
    {
        return player1Fence6;
    }

    public int getPlayer1Fence7()
    {
        return player1Fence7;
    }

    public int getPlayer1Fence8()
    {
        return player1Fence8;
    }

    public int getPlayer1Fence9()
    {
        return player1Fence9;
    }

    public int getPlayer1Fence10()
    {
        return player1Fence10;
    }

    public int getPlayer2Fence1()
    {
        return player2Fence1;
    }

    public int getPlayer2Fence2()
    {
        return player2Fence2;
    }

    public int getPlayer2Fence3()
    {
        return player2Fence3;
    }

    public int getPlayer2Fence4()
    {
        return player2Fence4;
    }

    public int getPlayer2Fence5()
    {
        return player2Fence5;
    }

    public int getPlayer2Fence6()
    {
        return player2Fence6;
    }

    public int getPlayer2Fence7()
    {
        return player2Fence7;
    }

    public int getPlayer2Fence8()
    {
        return player2Fence8;
    }

    public int getPlayer2Fence9()
    {
        return player2Fence9;
    }

    public int getPlayer2Fence10()
    {
        return player2Fence10;
    }

    public String getState()
    {
        return state;
    }

    public Date getLastMoveAt()
    {
        return lastMoveAt;
    }

    public Boolean isPlayer1Turn()
    {
        return isPlayer1Turn;
    }
}
