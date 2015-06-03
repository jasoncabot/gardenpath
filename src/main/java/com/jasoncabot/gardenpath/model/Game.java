package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.Optional;

import static java.util.Arrays.asList;

public class Game
{
    public static final int NUMBER_OF_SQUARES = 9;
    public static final int NUMBER_OF_FENCE_POSTS = NUMBER_OF_SQUARES + 1;
    public static final int TOTAL_SQUARES = NUMBER_OF_SQUARES * NUMBER_OF_SQUARES;
    public static final int TOTAL_FENCE_POSTS = NUMBER_OF_FENCE_POSTS * NUMBER_OF_FENCE_POSTS;

    private long id;
    private Player me;
    private Player you;
    private boolean isMyTurn;
    private Instant lastMoveAt;
    private State state;
    private PrivateInfo privateInfo;

    private Game()
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

        asList(Player.fromMemento(memento, true), Player.fromMemento(memento, false))
                .stream()
                .filter(Optional::isPresent)
                .forEach(optional -> {
                    final Player player = optional.get();
                    if (myIdentifier.equals(player.getIdentifier()))
                    {
                        game.me = player;
                    }
                    else
                    {
                        game.you = player;
                    }
                });

        if (game.me == null)
        {
            throw new IllegalArgumentException("No players have id of " + myIdentifier);
        }

        game.isMyTurn = game.me.isPlayerOne() && memento.isPlayer1Turn();

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

    public Instant getLastMoveAt()
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

    public enum State
    {
        UNKNOWN,
        WAITING_OPPONENT,
        IN_PROGRESS,
        GAME_OVER
    }
}
