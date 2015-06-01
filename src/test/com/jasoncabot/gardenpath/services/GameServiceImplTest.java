package com.jasoncabot.gardenpath.services;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
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
        when(mockDao.findAll()).thenReturn(null);
        final Collection<Game> games = service.findPublicGames();
        assertThat(games).isNotNull();
        assertThat(games).hasSize(0);
    }

    @Test
    public void shouldConvertAllDaoMementosIntoProperGameObjects() throws Exception
    {
        final List<GameMemento> expectedGames = asList(new GameMemento(), new GameMemento(), new GameMemento());
        when(mockDao.findAll()).thenReturn(expectedGames);
        final Collection<Game> actualGames = service.findPublicGames();
        assertThat(actualGames).hasSameSizeAs(expectedGames);
    }

}