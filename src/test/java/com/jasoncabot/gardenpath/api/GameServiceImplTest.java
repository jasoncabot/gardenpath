package com.jasoncabot.gardenpath.api;

import com.jasoncabot.gardenpath.core.Game;
import com.jasoncabot.gardenpath.db.GameDao;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {
    private GameService service;

    @Mock
    private GameDao mockDao;

    @Mock
    private Game game;

    @Before
    public void setUp() throws Exception {
        service = new GameServiceImpl(mockDao);
    }

    @Test
    public void shouldNeverReturnNullFromFindPublicGames() throws Exception {
        when(mockDao.findAll(anyString())).thenReturn(null);
        final Collection<Game> games = service.findPublicGames();
        assertThat(games, is(notNullValue()));
    }


    @Test
    public void shouldFindAllGamesWaitingForOpponent() {
        final List<Game> gamesFromDao = Stream.of(game, game, game).collect(Collectors.toList());
        when(mockDao.findAll("WAITING_OPPONENT")).thenReturn(gamesFromDao);
        final Collection<Game> actualGames = service.findPublicGames();
        assertThat(actualGames.size(), is(3));
    }

}