package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.GameDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        final List<Game> games = Optional.ofNullable(dao.findAll())
                .orElse(new ArrayList<>()).stream()
                .map(Game::fromMemento)
                .collect(Collectors.toList());
        logger.exiting(GameServiceImpl.class.getName(), "findPublicGames");
        return games;
    }
}
