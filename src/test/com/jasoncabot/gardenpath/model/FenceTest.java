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
import static com.jasoncabot.gardenpath.model.Game.TOTAL_SQUARES;
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
    public void shouldConvertFromUniqueIdentifierIntoUniqueFence()
    {
        final List<Fence> all = validFences();
        final Set<Fence> noDuplicates = new HashSet<>(all);

        assertThat(all).hasSameSizeAs(noDuplicates);
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
        assertThat(validFences().stream().filter(Fence::isVertical).count()).isEqualTo(64);
        assertThat(validFences().stream().filter(Fence::isHorizontal).count()).isEqualTo(64);
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

    @Test
    public void shouldBlockMovementThroughFenceButNothingElse()
    {
        final SoftAssertions softly = new SoftAssertions();

        validFences().stream().forEach(fence -> {
            int midpoint = fence.getMidpointIndex();
            int topRight = (((midpoint / NUMBER_OF_FENCE_POSTS) - 1) * NUMBER_OF_SQUARES) + (midpoint % NUMBER_OF_FENCE_POSTS);
            int bottomRight = ((midpoint / NUMBER_OF_FENCE_POSTS) * NUMBER_OF_SQUARES) + (midpoint % NUMBER_OF_FENCE_POSTS);
            int topLeft = topRight - 1;
            int bottomLeft = bottomRight - 1;

            if (fence.isHorizontal())
            {
                softly.assertThat(fence.blocksMove(topLeft, bottomLeft))
                        .as("Move from " + topLeft + " to " + bottomLeft + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(topRight, bottomRight))
                        .as("Move from " + topRight + " to " + bottomRight + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(bottomLeft, topLeft))
                        .as("Move from " + bottomLeft + " to " + topLeft + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(bottomRight, topRight))
                        .as("Move from " + bottomRight + " to " + topRight + " should be blocked by " + fence).isTrue();

            }
            else if (fence.isVertical())
            {
                softly.assertThat(fence.blocksMove(topLeft, topRight))
                        .as("Move from " + topLeft + " to " + topRight + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(topRight, topLeft))
                        .as("Move from " + topRight + " to " + topLeft + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(bottomLeft, bottomRight))
                        .as("Move from " + bottomLeft + " to " + bottomRight + " should be blocked by " + fence).isTrue();
                softly.assertThat(fence.blocksMove(bottomRight, bottomLeft))
                        .as("Move from " + bottomRight + " to " + bottomLeft + " should be blocked by " + fence).isTrue();
            }

            // Try every other possible move on the board, even though there will be plenty of moves that are not valid
            // it's a good exhaustive test for just the fence blocking behaviour to ensure no weirdness
            for (int a = 0; a < TOTAL_SQUARES; a++)
            {
                for (int b = 0; b < TOTAL_SQUARES; b++)
                {
                    boolean isBlockedMove = (fence.isHorizontal()
                            && (a == topLeft && b == bottomLeft)
                            || (a == topRight && b == bottomRight)
                            || (a == bottomLeft && b == topLeft)
                            || (a == bottomRight && b == topRight))
                            || (fence.isVertical()
                            && (a == topLeft && b == topRight)
                            || (a == topRight && b == topLeft)
                            || (a == bottomLeft && b == bottomRight)
                            || (a == bottomRight && b == bottomLeft));

                    if (!isBlockedMove)
                    {
                        softly.assertThat(fence.blocksMove(a, b))
                                .as("Move from " + a + " to " + b + " should not be blocked by " + fence).isFalse();
                    }
                }
            }
        });

        softly.assertAll();
    }

    private List<Fence> validFences()
    {
        final List<Fence> all = new ArrayList<>(4950);
        for (int startingPost = 0; startingPost < TOTAL_FENCE_POSTS; startingPost++)
        {
            for (int endingPost = startingPost; endingPost < TOTAL_FENCE_POSTS; endingPost++)
            {
                final int uniqueId = (startingPost * TOTAL_FENCE_POSTS) + endingPost;
                final Fence fence = Fence.get(uniqueId);
                if (fence.isValid())
                {
                    all.add(fence);
                }
            }
        }

        return all;
    }
}