package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.model.Game;

import java.util.Collection;

public interface GameService
{
    Collection<Game> findPublicGames();

    Game createPublicGame(final String playerId, final String playerName);

    Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword);

    Game joinPublicGame(final long gameId, final String playerId, final String playerName);

    Game joinPrivateGame(final long gameId, final String playerId, final String playerName, final String gameName, final String gamePassword);
}