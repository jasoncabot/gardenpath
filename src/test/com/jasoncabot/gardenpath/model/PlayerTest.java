package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlayerTest
{
    @Mock
    private GameMemento memento;

    @Before
    public void setUp()
    {
        initMocks(this);
        when(memento.getPlayer1Id()).thenReturn("id1");
        when(memento.getPlayer1Name()).thenReturn("name1");
        when(memento.getPlayer1Position()).thenReturn(1);
        when(memento.getPlayer2Id()).thenReturn("id2");
        when(memento.getPlayer2Name()).thenReturn("name2");
        when(memento.getPlayer2Position()).thenReturn(2);

        // 10 'in play' fences
        when(memento.getPlayer1Fence1()).thenReturn(1011);
        when(memento.getPlayer1Fence2()).thenReturn(1012);
        when(memento.getPlayer1Fence3()).thenReturn(1013);
        when(memento.getPlayer1Fence4()).thenReturn(1014);
        when(memento.getPlayer1Fence5()).thenReturn(1015);
        when(memento.getPlayer1Fence6()).thenReturn(1016);
        when(memento.getPlayer1Fence7()).thenReturn(1017);
        when(memento.getPlayer1Fence8()).thenReturn(1018);
        when(memento.getPlayer1Fence9()).thenReturn(1019);
        when(memento.getPlayer1Fence10()).thenReturn(1020);

        // 10 'in play' fences
        when(memento.getPlayer2Fence1()).thenReturn(2021);
        when(memento.getPlayer2Fence2()).thenReturn(2022);
        when(memento.getPlayer2Fence3()).thenReturn(2023);
        when(memento.getPlayer2Fence4()).thenReturn(2024);
        when(memento.getPlayer2Fence5()).thenReturn(2025);
        when(memento.getPlayer2Fence6()).thenReturn(2026);
        when(memento.getPlayer2Fence7()).thenReturn(2027);
        when(memento.getPlayer2Fence8()).thenReturn(2028);
        when(memento.getPlayer2Fence9()).thenReturn(2029);
        when(memento.getPlayer2Fence10()).thenReturn(2030);
    }

    @Test
    public void shouldReadPlayer1DataFromMemento()
    {
        final Player me = Player.fromMemento(memento, true).get();
        assertThat(me.getIdentifier()).isEqualTo("id1");
        assertThat(me.getName()).isEqualTo("name1");
        assertThat(me.getPosition()).isEqualTo(1);
    }

    @Test
    public void shouldReadPlayer2DataFromMemento()
    {
        final Player me = Player.fromMemento(memento, false).get();
        assertThat(me.getIdentifier()).isEqualTo("id2");
        assertThat(me.getName()).isEqualTo("name2");
        assertThat(me.getPosition()).isEqualTo(2);
    }

    @Test
    public void shouldReadPlayer1FenceDataFromMemento()
    {
        final Player me = Player.fromMemento(memento, true).get();
        assertThat(me.getFences().stream().mapToInt(Fence::hashCode).toArray())
                .containsSequence(1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020);
    }

    @Test
    public void shouldReadPlayer2FenceDataFromMemento()
    {
        final Player me = Player.fromMemento(memento, false).get();
        assertThat(me.getFences().stream().mapToInt(Fence::hashCode).toArray())
                .containsSequence(2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030);
    }

    @Test
    public void shouldBeTheCorrectPlayerWhenDeserialisingFromMemento()
    {
        assertThat(Player.fromMemento(memento, true).get().isPlayerOne()).isTrue();
        assertThat(Player.fromMemento(memento, false).get().isPlayerOne()).isFalse();
    }

    @Test
    public void shouldNotHavePlayerIfIdentifierIsNotPresentInMemento()
    {
        when(memento.getPlayer1Id()).thenReturn(null);
        assertThat(Player.fromMemento(memento, true).isPresent()).isFalse();
    }

    @Test
    public void shouldNotHavePlayerIfIdentifierIsBlankInMemento()
    {
        when(memento.getPlayer1Id()).thenReturn("");
        assertThat(Player.fromMemento(memento, true).isPresent()).isFalse();
    }

    @Test
    public void shouldMovePlayersToTheirCorrectStartingPoint()
    {
        final Player one = Player.builder().setPlayerOne().build();
        one.moveToStart();
        assertThat(one.getPosition()).isEqualTo(4);
        final Player two = Player.builder().build();
        two.moveToStart();
        assertThat(two.getPosition()).isEqualTo(76);
    }

    @Test
    public void shouldHaveCorrectWinningPositionsForPlayer1()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Player.Builder builder = Player.builder().setPlayerOne();
        for (int position = 0; position < 81; position++) {
            if (position >= 72 && position <= 80)
            {
                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p1 position " + position + " is winning").isTrue();
            }
            else
            {
                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p1 position " + position + " is not winning").isFalse();
            }
        }
        softly.assertAll();
    }

    @Test
    public void shouldHaveCorrectWinningPositionsForPlayer2()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Player.Builder builder = Player.builder();
        for (int position = 0; position < 81; position++) {
            if (position >= 0 && position <= 8)
            {
                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p2 position " + position + " is winning").isTrue();
            }
            else
            {
                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p2 position " + position + " is not winning").isFalse();
            }
        }
        softly.assertAll();
    }

    @Test
    public void shouldMovePlayerWhenUpdatingPosition()
    {
        final Player player = Player.builder().withPosition(5).build();
        player.updatePosition(50);
        assertThat(player.getPosition()).isEqualTo(50);
    }

    @Test
    public void shouldHaveFreeFenceWhenConstructingWithDefaultFence()
    {
        final Player player = Player.builder().withDefaultFences().build();
        assertThat(player.hasFreeFence()).isTrue();
    }

    @Test
    public void shouldHaveFreeFencesUntilTheLastIsPlayed()
    {
        final Fence validFence = Fence.get(8385);
        assertThat(validFence.isValid()).isTrue();

        final Player player = Player.builder().withDefaultFences().build();
        for (int i = 0; i < player.getFences().size(); i++)
        {
            assertThat(player.hasFreeFence()).isTrue();
            player.playFence(validFence);
        }
        assertThat(player.hasFreeFence()).isFalse();
    }
}