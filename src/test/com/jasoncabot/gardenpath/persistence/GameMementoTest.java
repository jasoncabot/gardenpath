package com.jasoncabot.gardenpath.persistence;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameMementoTest
{
    @Test
    public void shouldInitialiseWithUnknownState()
    {
        assertThat(new GameMemento().getState()).isEqualTo("UNKNOWN");
    }
}