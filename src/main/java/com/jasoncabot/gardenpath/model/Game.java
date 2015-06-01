package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.Validate;
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

    public static Game fromMemento(final GameMemento memento)
    {
        Validate.notNull(memento);

        final Game game = new Game();

        game.lastMoveAt = memento.getLastMoveAt();
        game.state = State.valueOf(memento.getState());
        game.id = memento.getId();
        game.privateInfo = PrivateInfo.fromHashed(memento.getName(), memento.getHashedPassphrase());

        return game;
    }

    public static Game fromMemento(final GameMemento memento, final String myIdentifier)
    {
        Validate.notBlank(myIdentifier);

        final Game game = fromMemento(memento);

        final Player one = Player.fromMemento(memento, true);
        final Player two = Player.fromMemento(memento, false);

        final boolean weArePlayer1 = myIdentifier.equals(one.getIdentifier());
        if (weArePlayer1)
        {
            game.me = one;
            game.you = two;
            game.isMyTurn = memento.isPlayer1Turn();
        }
        else
        {
            final boolean weArePlayer2 = myIdentifier.equals(two.getIdentifier());

            if (weArePlayer2)
            {
                game.me = two;
                game.you = one;
                game.isMyTurn = !memento.isPlayer1Turn();
            }
            else
            {
                throw new IllegalArgumentException("No players have id of " + myIdentifier);
            }
        }

        return game;
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
