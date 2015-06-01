package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;

public class Game
{
    private long id;

    public long getId()
    {
        return 1L;
    }

    public static Game fromMemento(final GameMemento memento)
    {
        return new Game();
    }

}
