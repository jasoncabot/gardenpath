package com.jasoncabot.gardenpath.resources;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GameResourceTest
{
    private GameResource resource;

    @Mock
    private GameService mockGameService;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        resource = new GameResource(mockGameService);
    }

    @Test
    public void shouldAskServiceForPublicGames() throws Exception
    {
        resource.listGames();
        verify(mockGameService).findPublicGames();
    }

    @Test
    public void shouldListPublicGamesWhenAskingToListAllGames() throws Exception
    {
        final Collection<Game> games = Collections.singletonList(new Game());
        when(mockGameService.findPublicGames()).thenReturn(games);
        assertThat(resource.listGames()).isEqualTo(games);
    }
}