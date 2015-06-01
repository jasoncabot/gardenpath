package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest
{
    @Test
    public void aNewlyCreatedGameShouldBeInUnknownState()
    {
        assertThat(new Game().getState()).isEqualTo(Game.State.UNKNOWN);
    }

}