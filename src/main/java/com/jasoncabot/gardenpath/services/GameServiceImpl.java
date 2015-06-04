package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Fence;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.Player;
import com.jasoncabot.gardenpath.model.PrivateInfo;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.persistence.GameMemento;

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
                .map(memento -> Game.builder().withAnonymousMemento(memento).build())
                .collect(Collectors.toList());
        logger.exiting(GameServiceImpl.class.getName(), "findPublicGames");
        return games;
    }

    @Override
    public Game createPublicGame(final String playerId, final String playerName)
    {
        logger.entering(GameServiceImpl.class.getName(), "createPublicGame", new Object[] { playerId, playerName });
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().setPlayerOne().build();
        final Game game = Game.builder().withMe(me).build();
        game.start();
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "createPublicGame", game);
        return game;
    }

    @Override
    public Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword)
    {
        logger.entering(GameServiceImpl.class.getName(), "createPrivateGame", new Object[] { playerId, playerName, gameName });
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().setPlayerOne().build();
        final Game game = Game.builder().withMe(me).withPrivateInfo(PrivateInfo.fromPlaintext(gameName, gamePassword)).build();
        game.start();
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "createPrivateGame", game);
        return game;
    }

    @Override
    public Game joinPublicGame(final long gameId, final String playerId, final String playerName)
    {
        logger.entering(GameServiceImpl.class.getName(), "joinPublicGame", new Object[] { gameId, playerId, playerName });
        final GameMemento memento = dao.find(gameId);
        final Game game = Game.builder().withMemento(memento, playerId).build();
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().build();
        game.join(me);
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "joinPublicGame", game);
        return game;
    }

    @Override
    public Game joinPrivateGame(String gameName, String gamePassword, String playerId, String playerName)
    {
        logger.entering(GameServiceImpl.class.getName(), "joinPrivateGame", new Object[] { gameName, playerId, playerName });
        final GameMemento memento = dao.find(PrivateInfo.fromPlaintext(gameName, gamePassword));
        final Game game = Game.builder().withMemento(memento, playerId).build();
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().build();
        game.join(me);
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "joinPrivateGame", game);
        return game;
    }

    @Override
    public Game addFence(final long gameId, final String playerId, final int start, final int end)
    {
        logger.entering(GameServiceImpl.class.getName(), "addFence", new Object[] { gameId, playerId, start, end });
        final GameMemento memento = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        final Game game = Game.builder().withMemento(memento, playerId).build();
        game.fence(Fence.get(start, end));
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "addFence", game);
        return game;
    }

    @Override
    public Game move(final long gameId, final String playerId, final int end)
    {
        logger.entering(GameServiceImpl.class.getName(), "move", new Object[] { gameId, playerId, end });
        final GameMemento memento = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        final Game game = Game.builder().withMemento(memento, playerId).build();
        game.move(end);
        dao.save(game);
        logger.exiting(GameServiceImpl.class.getName(), "move", game);
        return game;
    }
}
