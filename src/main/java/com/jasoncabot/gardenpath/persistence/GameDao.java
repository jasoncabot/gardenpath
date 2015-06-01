package com.jasoncabot.gardenpath.persistence;

import java.util.Collection;
import java.util.Collections;

public class GameDao
{
    public Collection<GameMemento> findAll()
    {
        return Collections.singletonList(new GameMemento());
    }
}
