package com.jasoncabot.gardenpath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.jasoncabot.gardenpath.model.Game.NUMBER_OF_FENCE_POSTS;
import static com.jasoncabot.gardenpath.model.Game.NUMBER_OF_SQUARES;
import static com.jasoncabot.gardenpath.model.Game.TOTAL_FENCE_POSTS;

public class Fence
{
    public static final int LENGTH = 2;

    private int startIndex = -1;
    private int endIndex = -1;

    public static Fence get(int id)
    {
        // convert our unique id back into start and end indexes
        int s = id % TOTAL_FENCE_POSTS;
        int e = id / TOTAL_FENCE_POSTS % TOTAL_FENCE_POSTS;
        return Fence.get(s, e);
    }

    public static Fence get(int start, int end)
    {
        final Fence fence = new Fence();

        // ensure start always smaller than end
        if (start < end)
        {
            fence.startIndex = start;
            fence.endIndex = end;
        }
        else
        {
            fence.startIndex = end;
            fence.endIndex = start;
        }
        return fence;
    }

    public static boolean blocks(final Fence one, final Fence two)
    {
        if (one.isHorizontal())
        {
            if (two.isVertical())
            {
                // we can only interfere in 1 place
                return one.startIndex == two.startIndex + NUMBER_OF_SQUARES;
            }
            else
            {
                // 3 possible interferences
                return (two.startIndex == one.startIndex) ||
                        (two.startIndex + 1 == one.startIndex) ||
                        (two.startIndex - 1 == one.startIndex);
            }
        }
        else
        {
            if (two.isHorizontal())
            {
                // we can only interfere in 1 place
                return one.startIndex + NUMBER_OF_SQUARES == two.startIndex;
            }
            else
            {
                // 3 possible interferences
                return (two.startIndex == one.startIndex) ||
                        (two.startIndex + NUMBER_OF_FENCE_POSTS == one.startIndex) ||
                        (two.startIndex - NUMBER_OF_FENCE_POSTS == one.startIndex);
            }
        }
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    @JsonIgnore
    public boolean isVertical()
    {
        return startIndex + 20 == endIndex;
    }

    @JsonIgnore
    public boolean isHorizontal()
    {
        return startIndex + 2 == endIndex;
    }

    @JsonIgnore
    public boolean isValid()
    {
        // start will always be less then end, this is ensured when we construct this fence
        if (startIndex == endIndex)
        {
            return false;
        }

        // basic validation
        if (!(startIndex == (endIndex - (NUMBER_OF_FENCE_POSTS * LENGTH)) || startIndex == (endIndex - LENGTH)))
        {
            return false;
        }

        if (isVertical())
        {
            // we cannot be on the left or right wall
            if (((startIndex % NUMBER_OF_FENCE_POSTS) == 0) || ((startIndex + 1) % NUMBER_OF_FENCE_POSTS) == 0)
            {
                return false;
            }
        }
        else if (isHorizontal())
        {
            // we cannot be on the top or bottom wall
            // if our fence is on the top wall and we horizontal
            if (startIndex < NUMBER_OF_FENCE_POSTS || startIndex >= (NUMBER_OF_FENCE_POSTS * NUMBER_OF_SQUARES))
            {
                return false;
            }

            // we cannot span/loop over the right hand wall
            for (int i = 1; i <= LENGTH; i++)
            {
                if (((startIndex + i) % NUMBER_OF_FENCE_POSTS) == 0)
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Fence fence = (Fence) o;

        return new EqualsBuilder()
                .append(startIndex, fence.startIndex)
                .append(endIndex, fence.endIndex)
                .isEquals();
    }

    public boolean blocksFence(final Fence other)
    {
        return blocks(this, other);
    }

    @Override
    public int hashCode()
    {
        return (startIndex * TOTAL_FENCE_POSTS) + endIndex;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", hashCode())
                .append("startIndex", startIndex)
                .append("endIndex", endIndex)
                .toString();
    }
}
