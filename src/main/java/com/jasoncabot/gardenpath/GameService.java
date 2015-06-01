package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.model.Game;

import java.util.Collection;

public interface GameService
{
    Collection<Game> findPublicGames();
}