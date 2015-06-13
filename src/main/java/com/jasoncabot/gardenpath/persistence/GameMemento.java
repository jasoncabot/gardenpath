package com.jasoncabot.gardenpath.persistence;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.Player;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class GameMemento
{
    private static final String DEFAULT_STATE = "UNKNOWN";
    public static String[] UPDATEABLE_FIELDS = new String[] {
            "state", "last_move_at", "name", "hashed_password", "is_player_one_turn",
            "p1_id", "p1_name", "p1_position", "p1_f1", "p1_f2", "p1_f3", "p1_f4", "p1_f5", "p1_f6", "p1_f7", "p1_f8", "p1_f9", "p1_f10",
            "p2_id", "p2_name", "p2_position", "p2_f1", "p2_f2", "p2_f3", "p2_f4", "p2_f5", "p2_f6", "p2_f7", "p2_f8", "p2_f9", "p2_f10"
    };
    protected static final String FIELDS_FOR_SELECTION = Stream.of(GameMemento.UPDATEABLE_FIELDS).collect(Collectors.joining(", "));
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
    private Instant lastMoveAt;
    private boolean isPlayer1Turn;

    public GameMemento()
    {
        state = DEFAULT_STATE;
    }

    public static GameMemento fromGame(final Game game)
    {
        final GameMemento memento = new GameMemento();

        if (game.getId() != null)
        {
            memento.setId(game.getId());
        }
        memento.setState(game.getState().toString());
        memento.setLastMoveAt(Instant.ofEpochMilli(game.getLastMoveAt()));
        memento.setIsPlayer1Turn(((game.isMyTurn() && game.getMe() != null && game.getMe().isPlayerOne())
                || (!game.isMyTurn() && game.getYou() != null && game.getYou().isPlayerOne())));

        if (game.getPrivateInfo() != null)
        {
            memento.setName(game.getPrivateInfo().getName());
            memento.setHashedPassphrase(game.getPrivateInfo().getHashedPassword());
        }

        Player one = null;
        Player two = null;
        for (final Player player : asList(game.getMe(), game.getYou()))
        {
            if (player != null)
            {
                if (player.isPlayerOne())
                {
                    one = player;
                }
                else
                {
                    two = player;
                }
            }
        }

        if (one != null)
        {
            memento.setPlayer1Id(one.getIdentifier());
            memento.setPlayer1Name(one.getName());
            memento.setPlayer1Position(one.getPosition());
            memento.setPlayer1Fence1(one.getFences().get(0).hashCode());
            memento.setPlayer1Fence2(one.getFences().get(1).hashCode());
            memento.setPlayer1Fence3(one.getFences().get(2).hashCode());
            memento.setPlayer1Fence4(one.getFences().get(3).hashCode());
            memento.setPlayer1Fence5(one.getFences().get(4).hashCode());
            memento.setPlayer1Fence6(one.getFences().get(5).hashCode());
            memento.setPlayer1Fence7(one.getFences().get(6).hashCode());
            memento.setPlayer1Fence8(one.getFences().get(7).hashCode());
            memento.setPlayer1Fence9(one.getFences().get(8).hashCode());
            memento.setPlayer1Fence10(one.getFences().get(9).hashCode());
        }
        if (two != null)
        {
            memento.setPlayer2Id(two.getIdentifier());
            memento.setPlayer2Name(two.getName());
            memento.setPlayer2Position(two.getPosition());
            memento.setPlayer2Fence1(two.getFences().get(0).hashCode());
            memento.setPlayer2Fence2(two.getFences().get(1).hashCode());
            memento.setPlayer2Fence3(two.getFences().get(2).hashCode());
            memento.setPlayer2Fence4(two.getFences().get(3).hashCode());
            memento.setPlayer2Fence5(two.getFences().get(4).hashCode());
            memento.setPlayer2Fence6(two.getFences().get(5).hashCode());
            memento.setPlayer2Fence7(two.getFences().get(6).hashCode());
            memento.setPlayer2Fence8(two.getFences().get(7).hashCode());
            memento.setPlayer2Fence9(two.getFences().get(8).hashCode());
            memento.setPlayer2Fence10(two.getFences().get(9).hashCode());
        }

        return memento;
    }

    public long getId()
    {
        return id;
    }

    void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    void setName(String name)
    {
        this.name = name;
    }

    public String getPlayer1Name()
    {
        return player1Name;
    }

    void setPlayer1Name(String player1Name)
    {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name()
    {
        return player2Name;
    }

    void setPlayer2Name(String player2Name)
    {
        this.player2Name = player2Name;
    }

    public String getHashedPassphrase()
    {
        return hashedPassphrase;
    }

    void setHashedPassphrase(String hashedPassphrase)
    {
        this.hashedPassphrase = hashedPassphrase;
    }

    public String getPlayer1Id()
    {
        return player1Id;
    }

    void setPlayer1Id(String player1Id)
    {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id()
    {
        return player2Id;
    }

    void setPlayer2Id(String player2Id)
    {
        this.player2Id = player2Id;
    }

    public int getPlayer1Position()
    {
        return player1Position;
    }

    void setPlayer1Position(int player1Position)
    {
        this.player1Position = player1Position;
    }

    public int getPlayer2Position()
    {
        return player2Position;
    }

    void setPlayer2Position(int player2Position)
    {
        this.player2Position = player2Position;
    }

    public int getPlayer1Fence1()
    {
        return player1Fence1;
    }

    void setPlayer1Fence1(int player1Fence1)
    {
        this.player1Fence1 = player1Fence1;
    }

    public int getPlayer1Fence2()
    {
        return player1Fence2;
    }

    void setPlayer1Fence2(int player1Fence2)
    {
        this.player1Fence2 = player1Fence2;
    }

    public int getPlayer1Fence3()
    {
        return player1Fence3;
    }

    void setPlayer1Fence3(int player1Fence3)
    {
        this.player1Fence3 = player1Fence3;
    }

    public int getPlayer1Fence4()
    {
        return player1Fence4;
    }

    void setPlayer1Fence4(int player1Fence4)
    {
        this.player1Fence4 = player1Fence4;
    }

    public int getPlayer1Fence5()
    {
        return player1Fence5;
    }

    void setPlayer1Fence5(int player1Fence5)
    {
        this.player1Fence5 = player1Fence5;
    }

    public int getPlayer1Fence6()
    {
        return player1Fence6;
    }

    void setPlayer1Fence6(int player1Fence6)
    {
        this.player1Fence6 = player1Fence6;
    }

    public int getPlayer1Fence7()
    {
        return player1Fence7;
    }

    void setPlayer1Fence7(int player1Fence7)
    {
        this.player1Fence7 = player1Fence7;
    }

    public int getPlayer1Fence8()
    {
        return player1Fence8;
    }

    void setPlayer1Fence8(int player1Fence8)
    {
        this.player1Fence8 = player1Fence8;
    }

    public int getPlayer1Fence9()
    {
        return player1Fence9;
    }

    void setPlayer1Fence9(int player1Fence9)
    {
        this.player1Fence9 = player1Fence9;
    }

    public int getPlayer1Fence10()
    {
        return player1Fence10;
    }

    void setPlayer1Fence10(int player1Fence10)
    {
        this.player1Fence10 = player1Fence10;
    }

    public int getPlayer2Fence1()
    {
        return player2Fence1;
    }

    void setPlayer2Fence1(int player2Fence1)
    {
        this.player2Fence1 = player2Fence1;
    }

    public int getPlayer2Fence2()
    {
        return player2Fence2;
    }

    void setPlayer2Fence2(int player2Fence2)
    {
        this.player2Fence2 = player2Fence2;
    }

    public int getPlayer2Fence3()
    {
        return player2Fence3;
    }

    void setPlayer2Fence3(int player2Fence3)
    {
        this.player2Fence3 = player2Fence3;
    }

    public int getPlayer2Fence4()
    {
        return player2Fence4;
    }

    void setPlayer2Fence4(int player2Fence4)
    {
        this.player2Fence4 = player2Fence4;
    }

    public int getPlayer2Fence5()
    {
        return player2Fence5;
    }

    void setPlayer2Fence5(int player2Fence5)
    {
        this.player2Fence5 = player2Fence5;
    }

    public int getPlayer2Fence6()
    {
        return player2Fence6;
    }

    void setPlayer2Fence6(int player2Fence6)
    {
        this.player2Fence6 = player2Fence6;
    }

    public int getPlayer2Fence7()
    {
        return player2Fence7;
    }

    void setPlayer2Fence7(int player2Fence7)
    {
        this.player2Fence7 = player2Fence7;
    }

    public int getPlayer2Fence8()
    {
        return player2Fence8;
    }

    void setPlayer2Fence8(int player2Fence8)
    {
        this.player2Fence8 = player2Fence8;
    }

    public int getPlayer2Fence9()
    {
        return player2Fence9;
    }

    void setPlayer2Fence9(int player2Fence9)
    {
        this.player2Fence9 = player2Fence9;
    }

    public int getPlayer2Fence10()
    {
        return player2Fence10;
    }

    void setPlayer2Fence10(int player2Fence10)
    {
        this.player2Fence10 = player2Fence10;
    }

    public String getState()
    {
        return state;
    }

    void setState(String state)
    {
        this.state = state;
    }

    public Instant getLastMoveAt()
    {
        return lastMoveAt;
    }

    void setLastMoveAt(Instant lastMoveAt)
    {
        this.lastMoveAt = lastMoveAt;
    }

    public boolean isPlayer1Turn()
    {
        return isPlayer1Turn;
    }

    void setIsPlayer1Turn(boolean isPlayer1Turn)
    {
        this.isPlayer1Turn = isPlayer1Turn;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("player1Name", player1Name)
                .append("player2Name", player2Name)
                .append("hashedPassphrase", hashedPassphrase)
                .append("player1Id", player1Id)
                .append("player2Id", player2Id)
                .append("player1Position", player1Position)
                .append("player2Position", player2Position)
                .append("player1Fence1", player1Fence1)
                .append("player1Fence2", player1Fence2)
                .append("player1Fence3", player1Fence3)
                .append("player1Fence4", player1Fence4)
                .append("player1Fence5", player1Fence5)
                .append("player1Fence6", player1Fence6)
                .append("player1Fence7", player1Fence7)
                .append("player1Fence8", player1Fence8)
                .append("player1Fence9", player1Fence9)
                .append("player1Fence10", player1Fence10)
                .append("player2Fence1", player2Fence1)
                .append("player2Fence2", player2Fence2)
                .append("player2Fence3", player2Fence3)
                .append("player2Fence4", player2Fence4)
                .append("player2Fence5", player2Fence5)
                .append("player2Fence6", player2Fence6)
                .append("player2Fence7", player2Fence7)
                .append("player2Fence8", player2Fence8)
                .append("player2Fence9", player2Fence9)
                .append("player2Fence10", player2Fence10)
                .append("state", state)
                .append("lastMoveAt", lastMoveAt)
                .append("isPlayer1Turn", isPlayer1Turn)
                .toString();
    }
}
