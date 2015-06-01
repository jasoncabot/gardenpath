package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

public class Game
{
    private long id;
    private Player me;
    private Player you;
    private boolean isMyTurn;
    private Date lastMoveAt;
    private State state;
    private PrivateInfo privateInfo;

    public Game()
    {
        this.id = -1;
        this.state = State.UNKNOWN;
    }

    public long getId()
    {
        return id;
    }

    public Player getMe()
    {
        return me;
    }

    public Player getYou()
    {
        return you;
    }

    public boolean isMyTurn()
    {
        return isMyTurn;
    }

    public Date getLastMoveAt()
    {
        return lastMoveAt;
    }

    public State getState()
    {
        return state;
    }

    public static Game fromMemento(final GameMemento memento)
    {
        return new Game();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Game game = (Game) o;

        return new EqualsBuilder()
                .append(id, game.id)
                .append(state, game.state)
                .append(me, game.me)
                .append(you, game.you)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(me)
                .append(you)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("me", me)
                .append("you", you)
                .append("isMyTurn", isMyTurn)
                .append("lastMoveAt", lastMoveAt)
                .append("state", state)
                .toString();
    }

    enum State
    {
        UNKNOWN,
        WAITING_OPPONENT,
        IN_PROGRESS,
        GAME_OVER
    }
}
