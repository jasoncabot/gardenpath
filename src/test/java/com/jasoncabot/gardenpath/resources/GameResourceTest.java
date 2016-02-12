package com.jasoncabot.gardenpath.resources;


import com.jasoncabot.gardenpath.api.GameService;
import com.jasoncabot.gardenpath.core.Game;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameResourceTest {
    private GameResource resource;

    @Mock
    private GameService mockGameService;

    @Before
    public void setUp() throws Exception {
        resource = new GameResource(mockGameService);
    }

    @Test
    public void shouldAskServiceForPublicGames() throws Exception {
        resource.listGames();
        verify(mockGameService).findPublicGames();
    }

    @Test
    public void shouldListPublicGamesWhenAskingToListAllGames() throws Exception {
        final Collection<Game> games = Collections.singletonList(mock(Game.class));
        when(mockGameService.findPublicGames()).thenReturn(games);
        assertThat(resource.listGames(), is(games));
    }
}