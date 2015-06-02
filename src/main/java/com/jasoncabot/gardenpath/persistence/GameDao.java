package com.jasoncabot.gardenpath.persistence;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.PrivateInfo;
import org.apache.commons.lang3.Validate;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

public class GameDao
{
    public Stream<GameMemento> findAll()
    {
        return Stream.of(new GameMemento());
    }

    public GameMemento create(final String player1Id, final String player1Name)
    {
        return create(player1Id, player1Name, Optional.<PrivateInfo>empty());
    }

    public GameMemento create(final String player1Id, final String player1Name, final PrivateInfo info)
    {
        Validate.notNull(info);
        return create(player1Id, player1Name, Optional.of(info));
    }

    private GameMemento create(final String player1Id, final String player1Name, final Optional<PrivateInfo> info)
    {
        final GameMemento memento = new GameMemento();
        memento.setPlayer1Name(player1Name);
        memento.setPlayer1Id(player1Id);
        memento.setLastMoveAt(new Date());
        memento.setState(Game.State.WAITING_OPPONENT.toString());
        if (info.isPresent())
        {
            memento.setName(info.get().getName());
            memento.setHashedPassphrase(info.get().getHashedPassword());
        }
        return memento;

    }
}
