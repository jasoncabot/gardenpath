package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Fence;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.GameException;
import com.jasoncabot.gardenpath.model.Player;
import com.jasoncabot.gardenpath.model.PrivateInfo;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService
{
    private static final Logger logger = Logger.getLogger(GameServiceImpl.class);

    private final GameDao dao;

    public GameServiceImpl(final GameDao dao)
    {
        this.dao = dao;
    }

    @Override
    public Collection<Game> findPublicGames()
    {
        logger.trace("ENTER:findPublicGames()");
        final List<Game> games = dao.findAll(Game.State.WAITING_OPPONENT.toString())
                .map(memento -> Game.builder().withAnonymousMemento(memento).build())
                .collect(Collectors.toList());
        logger.trace(String.format("EXIT:findPublicGames(%s)", games));
        return games;
    }

    @Override
    public Game findGame(final long gameId, final String playerId)
    {
        logger.trace(String.format("ENTER:findGame(%s, %s)", gameId, playerId));
        final GameMemento memento = dao.find(gameId, playerId);
        final Game game = Game.builder().withMemento(memento, playerId).build();
        logger.trace(String.format("EXIT:findGame(%s)", game));
        return game;
    }

    @Override
    public Game createPublicGame(final String playerId, final String playerName)
    {
        logger.trace(String.format("ENTER:createPublicGame(%s, %s)", playerId, playerName));
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().setPlayerOne().build();
        final Game game = Game.builder().withMe(me).build();
        game.start();
        dao.save(game);
        logger.trace(String.format("EXIT:createPublicGame(%s)", game));
        return game;
    }

    @Override
    public Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword)
    {
        logger.trace(String.format("ENTER:createPrivateGame(%s, %s, %s, [omitted])", playerId, playerName, gameName));
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().setPlayerOne().build();
        final Game game = Game.builder().withMe(me).withPrivateInfo(PrivateInfo.fromPlaintext(gameName, gamePassword)).build();
        game.start();
        dao.save(game);
        logger.trace(String.format("EXIT:createPrivateGame(%s)", game));
        return game;
    }

    @Override
    public Game joinPublicGame(final long gameId, final String playerId, final String playerName) throws GameException
    {
        logger.trace(String.format("ENTER:joinPublicGame(%s, %s, %s)", gameId, playerId, playerName));
        final GameMemento memento = dao.find(gameId);
        final Game game = Game.builder().withMemento(memento, playerId).build();
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().build();
        game.join(me);
        dao.save(game);
        logger.trace(String.format("EXIT:joinPublicGame(%s)", game));
        return game;
    }

    @Override
    public Game joinPrivateGame(final String gameName, final String gamePassword, final String playerId, final String playerName) throws GameException
    {
        logger.trace(String.format("ENTER:joinPrivateGame(%s, [omitted], %s, %s)", gameName, playerId, playerName));
        final GameMemento memento = dao.find(PrivateInfo.fromPlaintext(gameName, gamePassword));
        final Game game = Game.builder().withMemento(memento, playerId).build();
        final Player me = Player.builder().withUserData(playerId, playerName).withDefaultFences().build();
        game.join(me);
        dao.save(game);
        logger.trace(String.format("EXIT:joinPrivateGame(%s)", game));
        return game;
    }

    @Override
    public Game addFence(final long gameId, final String playerId, final int start, final int end) throws GameException
    {
        logger.trace(String.format("ENTER:addFence(%s, %s, %s, %s)", gameId, playerId, start, end));
        final GameMemento memento = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        final Game game = Game.builder().withMemento(memento, playerId).build();
        game.fence(Fence.get(start, end));
        dao.save(game);
        logger.trace(String.format("EXIT:addFence(%s)", game));
        return game;
    }

    @Override
    public Game move(final long gameId, final String playerId, final int end) throws GameException
    {
        logger.trace(String.format("ENTER:move(%s, %s, %s)", gameId, playerId, end));
        final GameMemento memento = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        final Game game = Game.builder().withMemento(memento, playerId).build();
        game.move(end);
        dao.save(game);
        logger.trace(String.format("EXIT:move(%s)", game));
        return game;
    }
}
