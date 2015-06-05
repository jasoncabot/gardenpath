package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GameServiceImplTest
{
    private GameService service;

    @Mock
    private GameDao mockDao;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        service = new GameServiceImpl(mockDao);
    }

    @Test
    public void shouldNeverReturnNullFromFindPublicGames() throws Exception
    {
        when(mockDao.findAll(anyString())).thenReturn(Stream.empty());
        final Collection<Game> games = service.findPublicGames();
        assertThat(games).isNotNull();
        assertThat(games).hasSize(0);
    }

    @Test
    public void shouldConvertAllDaoMementosIntoProperGameObjects() throws Exception
    {
        final Stream<GameMemento> expectedGames = Stream.of(new GameMemento(), new GameMemento(), new GameMemento());
        when(mockDao.findAll(anyString())).thenReturn(expectedGames);
        final Collection<Game> actualGames = service.findPublicGames();
        assertThat(actualGames).hasSize(3);
    }

    @Test
    public void shouldFindAllGamesWaitingForOpponent()
    {
        final Stream<GameMemento> expectedGames = Stream.of(new GameMemento(), new GameMemento(), new GameMemento());
        when(mockDao.findAll(anyString())).thenReturn(Stream.empty());
        when(mockDao.findAll(Game.State.WAITING_OPPONENT.toString())).thenReturn(expectedGames);
        final Collection<Game> actualGames = service.findPublicGames();
        assertThat(actualGames).hasSize(3);
    }

}