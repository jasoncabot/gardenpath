package com.jasoncabot.gardenpath.core;


import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jasoncabot.gardenpath.core.Fence.LENGTH;
import static com.jasoncabot.gardenpath.core.Game.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FenceTest {

    @Test
    public void shouldGetTheSameFenceRegardlessOfStartAndEndIndexOrdering() {
        final Fence a = Fence.get(10, 20);
        final Fence b = Fence.get(20, 10);

        assertThat(a, is(b));
    }

    @Test
    public void shouldGetTheSameFenceWhenAskingForSameId() {
        final Fence a = Fence.get(12);
        final Fence b = Fence.get(12);

        assertThat(a, is(b));
    }

    @Test
    public void shouldConvertFromUniqueIdentifierIntoUniqueFence() {
        final List<Fence> all = validFences();
        final Set<Fence> noDuplicates = new HashSet<>(all);

        assertThat(all.size(), is(noDuplicates.size()));
    }

    @Test
    public void shouldConvertToAndFromIdentifier() {
        assertThat(Fence.get(1011).hashCode(), is(1011));
    }

    @Test
    public void shouldChangeIdentifierIfStartIsGreaterThanEnd() {
        assertThat(Fence.get(1001).hashCode(), is(110));
    }

    @Test
    public void shouldKnowIfFenceIsVertical() {
        assertThat(Fence.get(0, 20).isVertical(), is(true));
    }

    @Test
    public void shouldKnowIfFenceIsHorizontal() {
        assertThat(Fence.get(0, 2).isHorizontal(), is(true));
    }

    @Test
    public void shouldBeInvalidIfStartAndEndAreTheSame() {
        assertThat(Fence.get(0, 0).isValid(), is(false));
    }

    @Test
    public void shouldBeInvalidIfSpanIsNotTwoPostsInHorizontalOrVerticalDirection() {
        assertThat(Fence.get(10, 30).isValid(), is(false));
    }

    @Test
    public void shouldBeInvalidIfVerticalAndOnLeftOrRightWall() {
        for (int start = 0; start < 80; start += 10) {
            assertThat(Fence.get(start, start + 20).isValid(), is(false));
        }
        for (int start = 9; start < 89; start += 10) {
            assertThat(Fence.get(start, start + 20).isValid(), is(false));
        }
    }

    @Test
    public void shouldBeInvalidIfHorizontalAndOnTopOrBottomWall() {
        for (int start = 0; start < 8; start += 1) {
            assertThat(Fence.get(start, start + 2).isValid(), is(false));
        }
        for (int start = 90; start < 99; start += 1) {
            assertThat(Fence.get(start, start + 2).isValid(), is(false));
        }
    }

    @Test
    public void shouldBeInvalidIfHorizontalAndSpansOverTheRightHandWall() {
        for (int start = 8; start < 98; start += 10) {
            assertThat(Fence.get(start, start + 2).isValid(), is(false));
            assertThat(Fence.get(start + 1, start + 1 + 2).isValid(), is(false));
        }
    }

    @Test
    public void shouldBeTheCorrectNumberOfValidFences() {
        assertThat(validFences().stream().filter(Fence::isVertical).count(), is(64L));
        assertThat(validFences().stream().filter(Fence::isHorizontal).count(), is(64L));
    }

    @Test
    public void shouldBlockFenceInACross() {
        for (int verticalOffset = 0; verticalOffset < (NUMBER_OF_SQUARES * NUMBER_OF_FENCE_POSTS); verticalOffset += NUMBER_OF_FENCE_POSTS) {
            for (int verticalStart = verticalOffset + 1; verticalStart < verticalOffset + NUMBER_OF_SQUARES; verticalStart++) {
                int horizontalStart = (verticalStart + NUMBER_OF_FENCE_POSTS) - 1;

                final Fence vertical = Fence.get(verticalStart, verticalStart + (LENGTH * NUMBER_OF_FENCE_POSTS));
                final Fence horizontal = Fence.get(horizontalStart, horizontalStart + LENGTH);

                assertThat(vertical.blocksFence(horizontal), is(true));
                assertThat(horizontal.blocksFence(vertical), is(true));
            }
        }
    }

    @Test
    public void shouldBlockMovementThroughFenceButNothingElse() {
        validFences().stream().parallel().forEach(fence -> {
            int midpoint = fence.getMidpointIndex();
            int topRight = (((midpoint / NUMBER_OF_FENCE_POSTS) - 1) * NUMBER_OF_SQUARES) + (midpoint % NUMBER_OF_FENCE_POSTS);
            int bottomRight = ((midpoint / NUMBER_OF_FENCE_POSTS) * NUMBER_OF_SQUARES) + (midpoint % NUMBER_OF_FENCE_POSTS);
            int topLeft = topRight - 1;
            int bottomLeft = bottomRight - 1;

            if (fence.isHorizontal()) {
                assertThat("Move from " + topLeft + " to " + bottomLeft + " should be blocked by " + fence, fence.blocksMove(topLeft, bottomLeft));
                assertThat("Move from " + topRight + " to " + bottomRight + " should be blocked by " + fence, fence.blocksMove(topRight, bottomRight));
                assertThat("Move from " + bottomLeft + " to " + topLeft + " should be blocked by " + fence, fence.blocksMove(bottomLeft, topLeft));
                assertThat("Move from " + bottomRight + " to " + topRight + " should be blocked by " + fence, fence.blocksMove(bottomRight, topRight));
            } else if (fence.isVertical()) {
                assertThat("Move from " + topLeft + " to " + topRight + " should be blocked by " + fence, fence.blocksMove(topLeft, topRight));
                assertThat("Move from " + topRight + " to " + topLeft + " should be blocked by " + fence, fence.blocksMove(topRight, topLeft));
                assertThat("Move from " + bottomLeft + " to " + bottomRight + " should be blocked by " + fence, fence.blocksMove(bottomLeft, bottomRight));
                assertThat("Move from " + bottomRight + " to " + bottomLeft + " should be blocked by " + fence, fence.blocksMove(bottomRight, bottomLeft));
            }

            // Try every other possible move on the board, even though there will be plenty of moves that are not valid
            // it's a good exhaustive test for just the fence blocking behaviour to ensure no weirdness
            for (int a = 0; a < TOTAL_SQUARES; a++) {
                for (int b = 0; b < TOTAL_SQUARES; b++) {
                    if (Game.adjacent(a, b)) {
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

                        if (!isBlockedMove) {
                            assertThat("Move from " + a + " to " + b + " should not be blocked by " + fence, fence.blocksMove(a, b), is(false));
                        }
                    }
                }
            }
        });
    }

    private List<Fence> validFences() {
        final List<Fence> all = new ArrayList<>(4950);
        for (int startingPost = 0; startingPost < TOTAL_FENCE_POSTS; startingPost++) {
            for (int endingPost = startingPost; endingPost < TOTAL_FENCE_POSTS; endingPost++) {
                final int uniqueId = (startingPost * TOTAL_FENCE_POSTS) + endingPost;
                final Fence fence = Fence.get(uniqueId);
                if (fence.isValid()) {
                    all.add(fence);
                }
            }
        }

        return all;
    }
}