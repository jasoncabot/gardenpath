package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;

import java.util.Collection;

import static java.util.Arrays.asList;

public class GameServiceImpl implements GameService
{
    @Override
    public Collection<Game> findPublicGames()
    {
        return asList(new Game());
    }
}
