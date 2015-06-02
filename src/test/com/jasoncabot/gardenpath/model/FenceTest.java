package com.jasoncabot.gardenpath.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jasoncabot.gardenpath.model.Fence.LENGTH;
import static com.jasoncabot.gardenpath.model.Game.NUMBER_OF_FENCE_POSTS;
import static com.jasoncabot.gardenpath.model.Game.NUMBER_OF_SQUARES;
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

    @Test
    public void shouldBeInvalidIfStartAndEndAreTheSame()
    {
        assertThat(Fence.get(0, 0).isValid()).isFalse();
    }

    @Test
    public void shouldBeInvalidIfSpanIsNotTwoPostsInHorizontalOrVerticalDirection()
    {
        assertThat(Fence.get(10, 30).isValid()).isFalse();
    }

    @Test
    public void shouldBeInvalidIfVerticalAndOnLeftOrRightWall()
    {
        for (int start = 0; start < 80; start += 10)
        {
            assertThat(Fence.get(start, start + 20).isValid()).isFalse();
        }
        for (int start = 9; start < 89; start += 10)
        {
            assertThat(Fence.get(start, start + 20).isValid()).isFalse();
        }
    }

    @Test
    public void shouldBeInvalidIfHorizontalAndOnTopOrBottomWall()
    {
        for (int start = 0; start < 8; start += 1)
        {
            assertThat(Fence.get(start, start + 2).isValid()).isFalse();
        }
        for (int start = 90; start < 99; start += 1)
        {
            assertThat(Fence.get(start, start + 2).isValid()).isFalse();
        }
    }

    @Test
    public void shouldBeInvalidIfHorizontalAndSpansOverTheRightHandWall()
    {
        for (int start = 8; start < 98; start += 10)
        {
            assertThat(Fence.get(start, start + 2).isValid()).isFalse();
            assertThat(Fence.get(start + 1, start + 1 + 2).isValid()).isFalse();
        }
    }

    @Test
    public void shouldBeTheCorrectNumberOfValidFences()
    {
        final List<Fence> all = new ArrayList<>(4950);
        for (int startingPost = 0; startingPost < TOTAL_FENCE_POSTS; startingPost++)
        {
            for (int endingPost = 0; endingPost < TOTAL_FENCE_POSTS; endingPost++)
            {
                if (endingPost > startingPost)
                {
                    final int uniqueId = (startingPost * TOTAL_FENCE_POSTS) + endingPost;
                    final Fence fence = Fence.get(uniqueId);
                    all.add(fence);
                }
            }
        }

        assertThat(all.stream().filter(Fence::isValid).filter(Fence::isHorizontal).count()).isEqualTo(64);
        assertThat(all.stream().filter(Fence::isValid).filter(Fence::isVertical).count()).isEqualTo(64);
    }

    @Test
    public void shouldBlockFenceInACross()
    {
        final SoftAssertions softly = new SoftAssertions();

        for (int verticalOffset = 0; verticalOffset < (NUMBER_OF_SQUARES * NUMBER_OF_FENCE_POSTS); verticalOffset += NUMBER_OF_FENCE_POSTS)
        {
            for (int verticalStart = verticalOffset + 1; verticalStart < verticalOffset + NUMBER_OF_SQUARES; verticalStart++)
            {
                int horizontalStart = (verticalStart + NUMBER_OF_FENCE_POSTS) - 1;

                final Fence vertical = Fence.get(verticalStart, verticalStart + (LENGTH * NUMBER_OF_FENCE_POSTS));
                final Fence horizontal = Fence.get(horizontalStart, horizontalStart + LENGTH);

                softly.assertThat(vertical.blocksFence(horizontal)).as(vertical + " should block " + horizontal).isTrue();
                softly.assertThat(horizontal.blocksFence(vertical)).as(horizontal + " should block " + vertical).isTrue();
            }
        }

        softly.assertAll();
    }

}