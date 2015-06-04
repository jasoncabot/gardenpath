package com.jasoncabot.gardenpath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class Game
{
    public static final int NUMBER_OF_SQUARES = 9;
    public static final int NUMBER_OF_FENCE_POSTS = NUMBER_OF_SQUARES + 1;
    public static final int TOTAL_SQUARES = NUMBER_OF_SQUARES * NUMBER_OF_SQUARES;
    public static final int TOTAL_FENCE_POSTS = NUMBER_OF_FENCE_POSTS * NUMBER_OF_FENCE_POSTS;

    private Long id;
    private Player me;
    private Player you;
    private boolean isMyTurn;
    private Instant lastMoveAt;
    private State state;
    private PrivateInfo privateInfo;

    private Game()
    {
        this.state = State.UNKNOWN;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
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

    @JsonIgnore
    public PrivateInfo getPrivateInfo()
    {
        return privateInfo;
    }

    public void join(final Player joiner)
    {
        Validate.notNull(joiner);

        if (me != null)
        {
            throw new IllegalArgumentException("Cannot join game you are already part of");
        }
        else if (you == null)
        {
            throw new IllegalArgumentException("Cannot join game without an opponent");
        }

        this.me = joiner;
        this.state = State.IN_PROGRESS;
        this.isMyTurn = me.isPlayerOne();
        this.lastMoveAt = Instant.now();
    }

    public void start()
    {
        this.state = State.WAITING_OPPONENT;
        this.lastMoveAt = Instant.now();
        this.isMyTurn = true;
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

    public static class Builder
    {
        private Long id;
        private Player me;
        private Player you;
        private boolean isMyTurn;
        private Instant lastMoveAt;
        private State state;
        private PrivateInfo privateInfo;

        public Builder withMe(final Player me)
        {
            this.me = me;
            return this;
        }

        public Builder withPrivateInfo(final PrivateInfo info)
        {
            this.privateInfo = info;
            return this;
        }

        public Builder withAnonymousMemento(final GameMemento memento)
        {
            Validate.notNull(memento);

            this.lastMoveAt = memento.getLastMoveAt();
            this.state = State.valueOf(memento.getState());
            this.id = memento.getId();
            this.privateInfo = PrivateInfo.fromHashed(memento.getName(), memento.getHashedPassphrase());

            return this;
        }

        public Builder withMemento(final GameMemento memento, final String myIdentifier)
        {
            this.withAnonymousMemento(memento);

            Stream.of(Player.fromMemento(memento, true), Player.fromMemento(memento, false))
                    .filter(Optional::isPresent)
                    .forEach(optional -> {
                        final Player player = optional.get();
                        if (myIdentifier != null && myIdentifier.equals(player.getIdentifier()))
                        {
                            this.me = player;
                        }
                        else
                        {
                            this.you = player;
                        }
                    });

            this.isMyTurn = this.me != null && this.me.isPlayerOne() && memento.isPlayer1Turn();
            return this;
        }

        public Game build()
        {
            final Game game = new Game();
            game.id = id;
            game.me = me;
            game.you = you;
            game.isMyTurn = isMyTurn;
            game.lastMoveAt = lastMoveAt;
            game.state = state;
            game.privateInfo = privateInfo;
            return game;
        }
    }
}
