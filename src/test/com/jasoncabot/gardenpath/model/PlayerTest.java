package com.jasoncabot.gardenpath.model;

import com.jasoncabot.gardenpath.persistence.GameMemento;
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
        final Player me = Player.fromMemento(memento, true);
        assertThat(me.getIdentifier()).isEqualTo("id1");
        assertThat(me.getName()).isEqualTo("name1");
        assertThat(me.getPosition()).isEqualTo(1);
    }

    @Test
    public void shouldReadPlayer2DataFromMemento()
    {
        final Player me = Player.fromMemento(memento, false);
        assertThat(me.getIdentifier()).isEqualTo("id2");
        assertThat(me.getName()).isEqualTo("name2");
        assertThat(me.getPosition()).isEqualTo(2);
    }

    @Test
    public void shouldReadPlayer1FenceDataFromMemento()
    {
        final Player me = Player.fromMemento(memento, true);
        assertThat(me.getFences().stream().mapToInt(Fence::hashCode).toArray())
                .containsSequence(1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020);
    }

    @Test
    public void shouldReadPlayer2FenceDataFromMemento()
    {
        final Player me = Player.fromMemento(memento, false);
        assertThat(me.getFences().stream().mapToInt(Fence::hashCode).toArray())
                .containsSequence(2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030);
    }

    @Test
    public void shouldBeTheCorrectPlayerWhenDeserialisingFromMemento()
    {
        assertThat(Player.fromMemento(memento, true).isPlayerOne()).isTrue();
        assertThat(Player.fromMemento(memento, false).isPlayerOne()).isFalse();
    }
}