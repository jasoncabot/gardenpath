package com.jasoncabot.gardenpath.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Fence
{

    public static final int BOARD_SIZE = 10;

    private int startIndex = -1;
    private int endIndex = -1;

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    public static Fence get(int id)
    {
        // convert our unique id back into start and end indexes
        int e = id % (BOARD_SIZE * BOARD_SIZE);
        int s = id / (BOARD_SIZE * BOARD_SIZE) % (BOARD_SIZE * BOARD_SIZE);
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
        return (startIndex * (BOARD_SIZE * BOARD_SIZE)) + endIndex;
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
