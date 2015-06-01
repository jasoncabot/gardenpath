package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FenceTest
{
    @Test
    public void shouldGetTheSameFenceRegardlessOfStartAndEndIndexOrdering()
    {
        final Fence a = Fence.get(10, 20);
        final Fence b = Fence.get(20, 10);

        assertThat(a).isEqualTo(b);
    }

    @Test
    public void shouldGetTheSameFenceWhenAskingForSameId()
    {
        final Fence a = Fence.get(12);
        final Fence b = Fence.get(12);

        assertThat(a).isEqualTo(b);
    }

}