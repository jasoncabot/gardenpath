package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GameTest
{
    @Mock
    private GameMemento memento;

    @Before
    public void setUp()
    {
        initMocks(this);
        when(memento.getState()).thenReturn("UNKNOWN");
        when(memento.isPlayer1Turn()).thenReturn(true);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenMementoIsNullWhenCreatingGame()
    {
        Game.builder().withAnonymousMemento(null).build();
    }

    @Test
    public void shouldParseValidGameStateFromMemento()
    {
        for (final Game.State state : Game.State.values())
        {
            when(memento.getState()).thenReturn(state.toString());
            final Game constructed = Game.builder().withAnonymousMemento(memento).build();
            assertThat(constructed.getState()).isEqualTo(state);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreatingGameFromInvalidStateInMemento()
    {
        when(memento.getState()).thenReturn("INVALID_STATE_THAT_DOES_NOT_EXIST");
        Game.builder().withAnonymousMemento(memento).build();
    }

    @Test
    public void shouldCreateMeAndTurnIfIdentifierMatchesPlayer1()
    {
        final String playerId = "id-one";
        when(memento.getPlayer1Id()).thenReturn(playerId);
        final Game game = Game.builder().withMemento(memento, playerId).build();
        assertThat(game.getMe().getIdentifier()).isEqualTo(playerId);
        assertThat(game.isMyTurn()).isTrue();
    }

    @Test
    public void shouldCreateMeAndTurnIfIdentifierMatchesPlayer2()
    {
        final String playerId = "id-two";
        when(memento.getPlayer2Id()).thenReturn(playerId);
        final Game game = Game.builder().withMemento(memento, playerId).build();
        assertThat(game.getMe().getIdentifier()).isEqualTo(playerId);
        assertThat(game.isMyTurn()).isFalse();
    }
}