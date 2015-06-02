package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jasoncabot.gardenpath.model.Fence.LENGTH;
import static com.jasoncabot.gardenpath.model.Game.NUMBER_OF_FENCE_POSTS;
import static com.jasoncabot.gardenpath.model.Game.TOTAL_FENCE_POSTS;
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

    @Test
    public void shouldGenerateUniqueIdentifiersForEveryFence()
    {

        final List<Fence> all = new ArrayList<>();
        final Set<Fence> noDuplicates = new HashSet<>();

        for (int rows = 0; rows < NUMBER_OF_FENCE_POSTS; rows++)
        {
            for (int cols = 0; cols < (NUMBER_OF_FENCE_POSTS - LENGTH); cols++)
            {
                final int hStart = cols + (rows * NUMBER_OF_FENCE_POSTS);
                final int hEnd = hStart + LENGTH;

                final int vStart = rows + (cols * NUMBER_OF_FENCE_POSTS);
                final int vEnd = vStart + (LENGTH * NUMBER_OF_FENCE_POSTS);

                all.add(Fence.get(hStart, hEnd));
                all.add(Fence.get(vStart, vEnd));
                noDuplicates.add(Fence.get(hStart, hEnd));
                noDuplicates.add(Fence.get(vStart, vEnd));
            }
        }

        assertThat(noDuplicates).hasSameSizeAs(all);
    }

    @Test
    public void shouldConvertFromUniqueIdentifierIntoUniqueFence()
    {

        final List<Fence> all = new ArrayList<>();
        final Set<Fence> noDuplicates = new HashSet<>();

        for (int startingPost = 0; startingPost < TOTAL_FENCE_POSTS; startingPost++)
        {
            for (int endingPost = 0; endingPost < TOTAL_FENCE_POSTS; endingPost++)
            {
                if (endingPost > startingPost)
                {
                    final int uniqueId = (startingPost * TOTAL_FENCE_POSTS) + endingPost;
                    final Fence fence = Fence.get(uniqueId);
                    all.add(fence);
                    noDuplicates.add(fence);
                }
            }

        }

        assertThat(noDuplicates.size()).isEqualTo(all.size());
    }

    @Test
    public void shouldConvertToAndFromIdentifier()
    {
        assertThat(Fence.get(1011).hashCode()).isEqualTo(1011);
    }

    @Test
    public void shouldChangeIdentifierIfStartIsGreaterThanEnd()
    {
        assertThat(Fence.get(1001).hashCode()).isEqualTo(110);
    }

    @Test
    public void shouldKnowIfFenceIsVertical()
    {
        assertThat(Fence.get(0, 20).isVertical());
    }

    @Test
    public void shouldKnowIfFenceIsHorizontal()
    {
        assertThat(Fence.get(0, 2).isHorizontal());
    }
}