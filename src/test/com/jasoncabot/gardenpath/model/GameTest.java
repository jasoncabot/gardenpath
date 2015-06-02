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
        Game.fromMemento(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenNullIdentifierSpecifiedWhenCreatingGameWithPlayer()
    {
        Game.fromMemento(memento, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenEmptyIdentifierSpecifiedWhenCreatingGameWithPlayer()
    {
        Game.fromMemento(memento, "");
    }

    @Test
    public void shouldParseValidGameStateFromMemento()
    {
        for (final Game.State state : Game.State.values())
        {
            when(memento.getState()).thenReturn(state.toString());
            final Game constructed = Game.fromMemento(memento);
            assertThat(constructed.getState()).isEqualTo(state);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreatingGameFromInvalidStateInMemento()
    {
        when(memento.getState()).thenReturn("INVALID_STATE_THAT_DOES_NOT_EXIST");
        Game.fromMemento(memento);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCreatingGameFromMementoWithInvalidPlayer()
    {
        when(memento.getPlayer1Id()).thenReturn("id-one");
        when(memento.getPlayer2Id()).thenReturn("id-two");
        Game.fromMemento(memento, "id-invalid");
    }

    @Test
    public void shouldCreateMeAndTurnIfIdentifierMatchesPlayer1()
    {
        final String playerId = "id-one";
        when(memento.getPlayer1Id()).thenReturn(playerId);
        final Game game = Game.fromMemento(memento, playerId);
        assertThat(game.getMe().getIdentifier()).isEqualTo(playerId);
        assertThat(game.isMyTurn()).isTrue();
    }

    @Test
    public void shouldCreateMeAndTurnIfIdentifierMatchesPlayer2()
    {
        final String playerId = "id-two";
        when(memento.getPlayer2Id()).thenReturn(playerId);
        final Game game = Game.fromMemento(memento, playerId);
        assertThat(game.getMe().getIdentifier()).isEqualTo(playerId);
        assertThat(game.isMyTurn()).isFalse();
    }
}