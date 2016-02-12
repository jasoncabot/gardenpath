package com.jasoncabot.gardenpath.api;

import com.jasoncabot.gardenpath.core.*;
import com.jasoncabot.gardenpath.db.GameDao;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GameServiceImpl implements GameService {
    private static final Logger logger = Logger.getLogger(GameServiceImpl.class);

    private final GameDao dao;

    public GameServiceImpl(final GameDao dao) {
        this.dao = dao;
    }

    @Override
    public Collection<Game> findPublicGames() {
        logger.trace("ENTER:findPublicGames()");
        final Collection<Game> games = dao.findAll(Game.State.WAITING_OPPONENT.toString());
        logger.trace(String.format("EXIT:findPublicGames(%s)", games));
        return Optional.ofNullable(games).orElse(new ArrayList<>());
    }

    @Override
    public Game findGame(final long gameId, final String playerId) {
        logger.trace(String.format("ENTER:findGame(%s, %s)", gameId, playerId));
        final Game game = dao.find(gameId, playerId);
        logger.trace(String.format("EXIT:findGame(%s)", game));
        return game;
    }

    @Override
    public Game createPublicGame(final String playerId, final String playerName) {
        logger.trace(String.format("ENTER:createPublicGame(%s, %s)", playerId, playerName));
        final long id = dao.createPublicGame(playerId, playerName);
        final Game game = dao.find(id, playerId);
        logger.trace(String.format("EXIT:createPublicGame(%s)", game));
        return game;
    }

    @Override
    public Game createPrivateGame(final String playerId, final String playerName, final String gameName, final String gamePassword) {
        logger.trace(String.format("ENTER:createPrivateGame(%s, %s, %s, [omitted])", playerId, playerName, gameName));
        final PrivateInfo privateInfo = PrivateInfo.fromPlaintext(gameName, gamePassword);
        long id = dao.createPrivateGame(playerId, playerName, privateInfo.getName(), privateInfo.getHashedPassword());
        final Game game = dao.find(id, playerId);
        logger.trace(String.format("EXIT:createPrivateGame(%s)", game));
        return game;
    }

    @Override
    public Game joinPublicGame(final long gameId, final String playerId, final String playerName) throws GameException {
        logger.trace(String.format("ENTER:joinPublicGame(%s, %s, %s)", gameId, playerId, playerName));
        int rowCount = dao.joinPublicGame(new GameDao.StartGameData(gameId, playerId, playerName));
        if (rowCount == 0) {
            throw new GameException("Game no longer available");
        }
        final Game game = dao.find(gameId, playerId);
        logger.trace(String.format("EXIT:joinPublicGame(%s)", game));
        return game;
    }

    @Override
    public Game joinPrivateGame(final String gameName, final String gamePassword, final String playerId, final String playerName) throws GameException {
        logger.trace(String.format("ENTER:joinPrivateGame(%s, [omitted], %s, %s)", gameName, playerId, playerName));
        int rowCount = dao.joinPrivateGame(new GameDao.StartGameData(playerId, playerName, gameName, gamePassword));
        if (rowCount == 0) {
            throw new GameException("Private game not found");
        }
        final Game game = dao.find(gameName, gamePassword, playerId);
        logger.trace(String.format("EXIT:joinPrivateGame(%s)", game));
        return game;
    }

    @Override
    public Game addFence(final long gameId, final String playerId, final int start, final int end) throws GameException {
        logger.trace(String.format("ENTER:addFence(%s, %s, %s, %s)", gameId, playerId, start, end));
        final Game currentGame = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        currentGame.validateFence(Fence.get(start, end));
        final GameDao.FenceGameData fences = new GameDao.FenceGameData(currentGame.getMe().getFences());
        final String updatedState = currentGame.getState().toString();
        if (currentGame.getMe().isPlayerOne()) {
            dao.updatePlayerOneFences(gameId, playerId, fences, updatedState);
        } else {
            dao.updatePlayerTwoFences(gameId, playerId, fences, updatedState);
        }
        final Game updatedGame = dao.find(gameId, playerId);
        logger.trace(String.format("EXIT:addFence(%s)", updatedGame));
        return updatedGame;
    }

    @Override
    public Game move(final long gameId, final String playerId, final int end) throws GameException {
        logger.trace(String.format("ENTER:move(%s, %s, %s)", gameId, playerId, end));
        final Game currentGame = dao.find(gameId, playerId, Game.State.IN_PROGRESS.toString());
        currentGame.validateMove(end);
        final int position = currentGame.getMe().getPosition();
        final String updatedState = currentGame.getState().toString();
        if (currentGame.getMe().isPlayerOne()) {
            dao.updatePlayerOnePosition(gameId, playerId, position, updatedState);
        } else {
            dao.updatePlayerTwoPosition(gameId, playerId, position, updatedState);
        }
        final Game updatedGame = dao.find(gameId, playerId);
        logger.trace(String.format("EXIT:move(%s)", updatedGame));
        return updatedGame;
    }
}
