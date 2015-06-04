package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.GameException;

import java.util.Collection;

public interface GameService
{
    Collection<Game> findPublicGames();

    Game createPublicGame(final String playerId, final String playerName);

    Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword);

    Game joinPublicGame(final long gameId, final String playerId, final String playerName) throws GameException;

    Game joinPrivateGame(final String gameName, final String gamePassword, final String playerId, final String playerName) throws GameException;

    Game addFence(final long gameId, final String playerId, final int start, final int end) throws GameException;

    Game move(final long gameId, final String playerId, final int end) throws GameException;
}