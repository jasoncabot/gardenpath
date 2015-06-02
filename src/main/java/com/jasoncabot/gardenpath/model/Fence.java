package com.jasoncabot.gardenpath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
