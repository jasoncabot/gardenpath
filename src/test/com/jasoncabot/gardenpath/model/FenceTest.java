package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jasoncabot.gardenpath.model.Fence.BOARD_SIZE;
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

        for (int rows = 0; rows < 10; rows++)
        {
            for (int cols = 0; cols < 8; cols++)
            {
                final int hStart = cols + (rows * BOARD_SIZE);
                final int hEnd = hStart + 2;

                final int vStart = rows + (cols * BOARD_SIZE);
                final int vEnd = vStart + (2 * BOARD_SIZE);

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

        for (int a = 0; a < (BOARD_SIZE * BOARD_SIZE); a++)
        {
            for (int b = 0; b < (BOARD_SIZE * BOARD_SIZE); b++)
            {
                if (b > a)
                {
                    final int uniqueId = (a * (BOARD_SIZE * BOARD_SIZE)) + b;
                    final Fence fence = Fence.get(uniqueId);
                    all.add(fence);
                    noDuplicates.add(fence);
                }
            }

        }

        assertThat(noDuplicates.size()).isEqualTo(all.size());
    }

}