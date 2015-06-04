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
    private Game readyToBeJoined;

    @Mock
    private GameMemento memento;

    @Before
    public void setUp()
    {
        initMocks(this);
        when(memento.getState()).thenReturn("UNKNOWN");
        when(memento.isPlayer1Turn()).thenReturn(true);

        readyToBeJoined = Game.builder().withYou(Player.builder().withUserData("opponent", "person").withDefaultFences().setPlayerOne().build()).build();
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

    @Test(expected = GameException.class)
    public void shouldThrowExceptionWhenJoiningGameThatAlreadyHasPlayerAsMe() throws GameException
    {
        final Player me = Player.builder().withUserData("id", "name").build();
        final Player existingPlayer = Player.builder().withUserData("id", "name").build();
        final Game game = Game.builder().withMe(existingPlayer).build();
        game.join(me);
    }

    @Test(expected = GameException.class)
    public void shouldThrowExceptionWhenJoiningGameWithoutOpponent() throws GameException
    {
        final Player me = Player.builder().withUserData("p2", "me").build();
        final Game game = Game.builder().withYou(null).build();
        game.join(me);
    }

    @Test(expected = GameException.class)
    public void shouldThrowExceptionIfJoiningGameWithPlayerOneWhenPlayerOneAlreadyExists() throws GameException
    {
        final Player me = Player.builder().withUserData("me", "name").withDefaultFences().setPlayerOne().build();
        readyToBeJoined.join(me);
    }

    @Test
    public void shouldUpdateGameStateToInProgress() throws GameException
    {
        final Player me = Player.builder().withUserData("me", "name").withDefaultFences().build();
        readyToBeJoined.join(me);
        assertThat(readyToBeJoined.getState()).isEqualTo(Game.State.IN_PROGRESS);
    }

    @Test
    public void shouldNotBeMyTurn() throws GameException
    {
        final Player me = Player.builder().withUserData("me", "name").withDefaultFences().build();
        readyToBeJoined.join(me);
        assertThat(readyToBeJoined.isMyTurn()).isFalse();
    }

    @Test
    public void shouldUpdatePositionOfPlayersWhenJoining() throws GameException
    {
        final Player me = Player.builder().withUserData("me", "name").withDefaultFences().build();
        readyToBeJoined.join(me);
        assertThat(readyToBeJoined.getMe().getPosition()).isNotZero();
        assertThat(readyToBeJoined.getYou().getPosition()).isNotZero();
    }

    @Test
    public void shouldBeAdjacentToAllTouchingPositions()
    {
        assertThat(Game.adjacent(40, 31)).isTrue();
        assertThat(Game.adjacent(40, 49)).isTrue();
        assertThat(Game.adjacent(40, 39)).isTrue();
        assertThat(Game.adjacent(40, 41)).isTrue();
    }

    @Test
    public void shouldNotBeAdjacentIfWrappingAcrossWall()
    {
        assertThat(Game.adjacent(8, 9)).isFalse();
        assertThat(Game.adjacent(71, 72)).isFalse();
        assertThat(Game.adjacent(9, 8)).isFalse();
        assertThat(Game.adjacent(72, 71)).isFalse();
    }
}