package com.jasoncabot.gardenpath.persistence;

import java.util.stream.Stream;

public class GameDao
{
    public Stream<GameMemento> findAll()
    {
        return Stream.of(new GameMemento());
    }
}
