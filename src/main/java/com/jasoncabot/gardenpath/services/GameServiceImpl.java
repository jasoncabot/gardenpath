package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.PrivateInfo;
import com.jasoncabot.gardenpath.persistence.GameDao;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService
{
    private static final Logger logger = Logger.getLogger(GameServiceImpl.class.getName());

    private final GameDao dao;

    public GameServiceImpl(final GameDao dao)
    {
        this.dao = dao;
    }

    @Override
    public Collection<Game> findPublicGames()
    {
        logger.entering(GameServiceImpl.class.getName(), "findPublicGames");
        final List<Game> games = dao.findAll(Game.State.WAITING_OPPONENT.toString())
                .map(Game::fromMemento)
                .collect(Collectors.toList());
        logger.exiting(GameServiceImpl.class.getName(), "findPublicGames");
        return games;
    }

    @Override
    public Game createPublicGame(final String playerId, final String playerName)
    {
        logger.entering(GameServiceImpl.class.getName(), "createPublicGame", new Object[] { playerId, playerName });
        final Game game = Game.fromMemento(dao.create(playerId, playerName), playerId);
        logger.exiting(GameServiceImpl.class.getName(), "createPublicGame", game);
        return game;
    }

    @Override
    public Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword)
    {
        logger.entering(GameServiceImpl.class.getName(), "createPrivateGame", new Object[] { playerId, playerName, gameName });
        final Game game = Game.fromMemento(dao.create(playerId, playerName, PrivateInfo.fromPlaintext(gameName, gamePassword)), playerId);
        logger.exiting(GameServiceImpl.class.getName(), "createPrivateGame", game);
        return game;
    }

    @Override
    public Game joinPublicGame(final long gameId, final String playerId, final String playerName)
    {
        logger.entering(GameServiceImpl.class.getName(), "joinPublicGame", new Object[] { gameId, playerId, playerName });
//        final Game game = Game.fromMemento(dao.find(gameId));
//        game.join(Player.withId(playerId).withName(playerName).build());
//        dao.save(game.toMemento());
//        logger.exiting(GameServiceImpl.class.getName(), "joinPublicGame", game);
        return null;
    }

    @Override
    public Game joinPrivateGame(long gameId, String playerId, String playerName, String gameName, String gamePassword)
    {
        logger.entering(GameServiceImpl.class.getName(), "joinPrivateGame", new Object[] { gameId, playerId, playerName, gameName });
//        final Game game = Game.fromMemento(dao.find(gameId, PrivateInfo.fromPlaintext(gameName, gamePassword)));
//        game.join(Player.withId(playerId).withName(playerName).build());
//        dao.save(game.toMemento());
//        logger.exiting(GameServiceImpl.class.getName(), "joinPrivateGame", game);
        return null;
    }
}
