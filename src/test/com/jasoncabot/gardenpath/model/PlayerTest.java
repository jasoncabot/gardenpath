package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest
{
    @Test
    public void aNewlyCreatedPlayerShouldHave10Fences()
    {
        assertThat(new Player("id", "name").getFences()).hasSize(10);
    }
}